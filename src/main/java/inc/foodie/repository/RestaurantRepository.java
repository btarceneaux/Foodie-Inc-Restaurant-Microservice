package inc.foodie.repository;

import inc.foodie.bean.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer>
{
    Restaurant findByRestaurantName(String restaurantName);
}