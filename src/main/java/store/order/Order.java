package store.order;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Accessors(fluent = true)
public class Order{
    private String id;
    private String idAccount;
    private LocalDateTime date;
    private List<Item> items;
    private Double total;

}
