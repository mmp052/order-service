package store.order;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Setter
@Accessors(fluent = true)
@NoArgsConstructor
public class OrderModel {

    @Id
    @Column(name = "id_order")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "id_account")
    private String idAccount;

    @Column(name = "order_date")
    private LocalDateTime date;

    @Column(name = "total")
    private Double total;

    public OrderModel(Order a) {
        this.id = a.id();
        this.idAccount = a.idAccount();
        this.date = a.date();
        this.total = a.total();
    }

    public Order to() {
        return Order.builder()
                .id(this.id)
                .idAccount(this.idAccount)
                .date(this.date)
                .total(this.total)
                .build();
    }

}
