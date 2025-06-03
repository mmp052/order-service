package store.order;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "order_items")
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class ItemModel {
    @Id
    @Column(name = "id_order_item")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "id_order")
    private String idOrder;

    @Column(name = "id_product")
    private String idProduct;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total")
    private Double total;

    public ItemModel(Item a) {
        this.id = a.id();
        this.idOrder = a.idOrder();
        this.idProduct = a.idProduct();
        this.quantity = a.quantity();
        this.total = a.total();
    }

    public Item to() {
        return Item.builder()
                .id(this.id)
                .idOrder(this.idOrder)
                .idProduct(this.idProduct)
                .quantity(this.quantity)
                .total(this.total)
                .build();
    }
}
