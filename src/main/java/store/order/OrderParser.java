package store.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderParser {

    public static Order to(OrderIn in, String idAccount) {
        if (in == null) return null;

        return Order.builder()
                .idAccount(idAccount)
                .date(LocalDateTime.now())
                .items(in.items().stream().map(OrderParser::to).toList())
                .build();
    }

    public static Item to(ItemIn in) {
        if (in == null) return null;

        return Item.builder()
                .idProduct(in.idProduct())
                .quantity(in.quantity())
                .build();
    }

    public static OrderOut to(Order order) {
        if (order == null) return null;

        return OrderOut.builder()
                .id(order.id())
                .date(order.date())
                .items(order.items().stream().map(OrderParser::to).toList())
                .total(order.total())
                .build();
    }

    public static ItemOut to(Item item) {
        if (item == null) return null;

        return ItemOut.builder()
                .id(item.id()) // Assuming Item has an id field
                .product(ProductRef.builder()
                        .idProduct(item.idProduct())
                        .build())
                .quantity(item.quantity())
                .total(item.total()) // Assuming Item has a total field
                .build();
    }

    public static OrderOut to(Order order, List<Item> items, List<ProductOut> products) {
        if (order == null) return null;

        List<ItemOut> itemOuts = new ArrayList<>();
        for (Item item : items) {
            ProductOut product = products.stream()
                    .filter(p -> p.id().equals(item.idProduct()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                itemOuts.add(ItemOut.builder()
                        .id(item.id()) // Use item's id, not idProduct
                        .product(ProductRef.builder()
                                .idProduct(item.idProduct())
                                .build())
                        .quantity(item.quantity())
                        .total(item.total())
                        .build());
            }
        }

        return OrderOut.builder()
                .id(order.id())
                .date(order.date())
                .items(itemOuts)
                .total(order.total())
                .build();
    }

    public static OrderOut toSummary(Order order) {
        if (order == null) return null;

        List<ItemOut> itemOuts = order.items().stream()
                .map(OrderParser::to)
                .toList();

        return OrderOut.builder()
                .id(order.id())
                .date(order.date())
                .total(order.total())
                .items(itemOuts)
                .build();
    }
}