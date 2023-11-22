package inc.foodie.service;

import inc.foodie.bean.Restaurant;
import inc.foodie.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService
{
    @Autowired
    RestaurantRepository repository;

    public List<Restaurant> getAllRestaurants()
    {
        return repository.findAll();
    }

    public Optional<Restaurant> getRestaurantByRestaurantId(int restaurantId)
    {
        boolean exists = repository.existsById(restaurantId);
        if(exists)
        {
            return repository.findById(restaurantId);
        }
        else
        {
            return null;
        }
    }

    public Restaurant createRestaurant(Restaurant myRestaurant)
    {
        Restaurant tempRestaurant = repository.findByRestaurantName(myRestaurant.getRestaurantName());
        if(tempRestaurant != null)
        {
            return tempRestaurant;
        }
        else
        {
            return repository.save(myRestaurant);
        }
    }

    public int deleteRestaurant(int restaurantId)
    {
        int result = 0;
        boolean exists = repository.existsById(restaurantId);

        if(exists)
        {
            repository.deleteById(restaurantId);
            result = 1;
        }

        return result;
    }

    public Restaurant updateRestaurant(Restaurant updatedRestaurant)
    {
        boolean exists = repository.existsById(updatedRestaurant.getRestaurantId());
        if(exists)
        {
            return repository.save(updatedRestaurant);
        }
        else
        {
            return null;
        }
    }
}