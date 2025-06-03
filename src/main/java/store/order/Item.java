package store.order;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

@Builder
@Getter
@Data
@Accessors(fluent = true)
public class Item {
    private String id;
    private String idOrder;
    private String idProduct;
    private Integer quantity;
    private Double total;
}
