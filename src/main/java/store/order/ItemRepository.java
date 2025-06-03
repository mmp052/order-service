package store.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends CrudRepository<ItemModel, String> {
    List<ItemModel> findByIdOrder(String idOrder);
}
