package store.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict; // Importação adicionada
import org.springframework.cache.annotation.CachePut;   // Importação adicionada
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Cache para resultados de produtos vindos do microsserviço de produtos.
    // Chave: o ID do produto.
    @Cacheable(value = "products", key = "#id")
    private ProductOut findProductById(String id) {
        String url = "http://product:8080/product/" + id;
        try {
            ResponseEntity<ProductOut> response = restTemplate.getForEntity(url, ProductOut.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found from external service");
            }
        } catch (Exception e) {
            System.err.println("Error calling product service for ID " + id + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found or external service error");
        }
    }

    // Ao criar um pedido, precisamos invalidar os caches relacionados a pedidos.
    // 1. O cache da lista de pedidos por conta (findAllByAccount).
    // 2. Potencialmente o cache do pedido individual (embora um novo pedido não existisse no cache antes).
    @CacheEvict(value = "orders", allEntries = true) // Invalida todas as listas de pedidos por conta
    // @CachePut(value = "orders", key = "#result.id") // Opcional: Se quiser cachear o pedido recém-criado individualmente
    public OrderOut create(OrderIn orderIn, String idAccount){

        if (orderIn.items() == null || orderIn.items().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must have at least one item");
        }

        Order order = OrderParser.to(orderIn, idAccount);
        double orderTotal = 0.0;

        // Recuperar produtos e calcular totais
        List<Item> orderItems = new ArrayList<>();
        List<ProductOut> products = new ArrayList<>();

        for (ItemIn itemIn : orderIn.items()) {
            // Validação da quantidade
            if (itemIn.quantity() == null || itemIn.quantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item quantity must be greater than zero");
            }

            // O findProductById usa cache, o que é bom aqui.
            ProductOut product = findProductById(itemIn.idProduct());
            products.add(product);

            double itemTotal = product.price() * itemIn.quantity();

            // Criar item do pedido
            Item orderItem = Item.builder()
                    .idProduct(itemIn.idProduct())
                    .quantity(itemIn.quantity())
                    .total(itemTotal)
                    .build();

            orderItems.add(orderItem);
            orderTotal += itemTotal;
        }

        // Definir o total do pedido
        order.total(orderTotal);

        // Salvar o pedido
        OrderModel savedOrder = orderRepository.save(new OrderModel(order));
        order = savedOrder.to(); // Atualiza o objeto 'order' com o ID gerado pelo banco

        // Salvar os itens do pedido
        List<Item> savedItems = new ArrayList<>();
        for (Item item : orderItems) {
            item.idOrder(order.id()); // Associa o item ao ID do pedido salvo
            ItemModel savedItem = itemRepository.save(new ItemModel(item));
            savedItems.add(savedItem.to());
        }

        // Montar resposta
        return OrderParser.to(order, savedItems, products);
    }

    // Cache para a lista de pedidos de uma conta. Chave: o ID da conta.
    @Cacheable(value = "orders", key = "'accountOrders-' + #idAccount") // Chave composta para listas por conta
    public List<OrderOut> findAllByAccount(String idAccount) {
        System.out.println("Buscando todos os pedidos da conta " + idAccount + " do banco de dados.");
        List<OrderOut> result = new ArrayList<>();
        for (OrderModel orderModel : orderRepository.findByIdAccount(idAccount)) {
            Order order = orderModel.to();

            List<Item> items = new ArrayList<>();
            for (ItemModel itemModel : itemRepository.findByIdOrder(order.id())) {
                items.add(itemModel.to());
            }

            order.items(items); // popula os itens

            result.add(OrderParser.toSummary(order));
        }
        return result;
    }

    // Cache para um pedido individual. Chave: o ID do pedido.
    @Cacheable(value = "orders", key = "#id")
    public OrderOut findByIdAndAccount(String id, String idAccount) {
        System.out.println("Buscando pedido " + id + " para conta " + idAccount + " do banco de dados.");
        // Buscar o pedido
        OrderModel orderModel = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Order order = orderModel.to();

        // Verificar se o pedido pertence ao usuário atual
        if (!order.idAccount().equals(idAccount)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to order denied: " + id); // Use FORBIDDEN para acesso negado
        }

        // Buscar os itens do pedido
        List<Item> items = new ArrayList<>();
        for (ItemModel itemModel : itemRepository.findByIdOrder(id)) {
            items.add(itemModel.to());
        }

        List<ProductOut> products = new ArrayList<>();
        for (Item item : items) {
            ProductOut product = findProductById(item.idProduct());
            if (product != null) {
                products.add(product);
            }
        }


        return OrderParser.to(order, items, products);
    }


}