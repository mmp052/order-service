package store.order;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<OrderModel, String> {
    // Custom query methods can be defined here if needed
    List<OrderModel> findByIdAccount(String idAccount);
}
