package ac.at.fhcampuswien.carinventoryms.repository;

import ac.at.fhcampuswien.carinventoryms.models.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CarRepository extends MongoRepository<Car, String> {

    @Query(value = "{ id: { $nin: ?0 } }")
    List<Car> findCarsNotInList(List<String> ids);

    @Query(value = "{id: ?0}")
    Car getCarById(String id);
}
