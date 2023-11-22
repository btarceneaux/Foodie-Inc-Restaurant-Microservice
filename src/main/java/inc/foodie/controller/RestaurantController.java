package inc.foodie.controller;

import inc.foodie.bean.Restaurant;
import inc.foodie.dto.ResponseDto;
import inc.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class RestaurantController
{
    @Autowired
    RestaurantService service;

    @GetMapping("/restaurant")
    public List<Restaurant> getAllRestaurants()
    {
        return service.getAllRestaurants();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseDto getRestaurantByRestaurantId(@PathVariable int restaurantId)
    {
        ResponseDto response = new ResponseDto();
        Optional<Restaurant> optionalRestaurant = service.getRestaurantByRestaurantId(restaurantId);

        if(optionalRestaurant.isPresent())
        {
            Restaurant myCustomer = optionalRestaurant.get();
            response.setMessage("The restaurant was found.");
            response.setStatus(HttpStatus.OK.value());
            response.setTimestamp(new Date());
            response.setData(myCustomer);
        }
        else
        {
            response.setMessage("The restaurant does not exist.");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.setTimestamp(new Date());
            response.setData(null);
        }

        return response;
    }

    @PostMapping("/restaurant")
    public ResponseDto createRestaurant(@RequestBody Restaurant myRestaurant)
    {
        ResponseDto response = new ResponseDto();

        if(myRestaurant.getRestaurantName() == null)
        {
            response.setMessage("The restaurant was not successfully saved because one of the fields are blank.");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setTimestamp(new Date());
            response.setData(null);

            return response;
        }

        Restaurant savedRestaurant = service.createRestaurant(myRestaurant);

        if(savedRestaurant.getRestaurantId() > 0)
        {
            response.setMessage("The restaurant was successfully saved.");
            response.setStatus(HttpStatus.OK.value());
            response.setTimestamp(new Date());
            response.setData(savedRestaurant);
        }
        else
        {
            response.setMessage("The restaurant was not successfully saved.");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.setTimestamp(new Date());
            response.setData(null);
        }

        return response;
    }

    @PutMapping("/restaurant")
    public ResponseDto updateRestaurant(@RequestBody Restaurant myRestaurant)
    {
        ResponseDto response = new ResponseDto();

        Restaurant updatedRestaurant  = service.updateRestaurant(myRestaurant);

        response.setMessage("The restaurant was updated successfully.");
        response.setStatus(HttpStatus.OK.value());
        response.setTimestamp(new Date());
        response.setData(updatedRestaurant);

        return response;
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    public ResponseDto deleteRestaurant(@PathVariable int restaurantId)
    {
        ResponseDto response = new ResponseDto();

        int result = service.deleteRestaurant(restaurantId);
        if(result == 1)
        {
            response.setMessage("The restaurant was successfully deleted.");
            response.setStatus(HttpStatus.OK.value());
        }
        else
        {
            response.setMessage("The restaurant was not successfully deleted.");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
        }
        response.setTimestamp(new Date());
        response.setData(null);

        return response;
    }
}