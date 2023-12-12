package inc.foodie.controller;

import com.amazonaws.HttpMethod;
import com.mashape.unirest.http.Unirest;
import inc.foodie.bean.Dish;
import inc.foodie.bean.Restaurant;
import inc.foodie.dto.ResponseDto;
import inc.foodie.service.RestaurantService;
import inc.foodie.service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

@RestController
public class RestaurantController
{
    @Autowired
    RestaurantService service;

    @Autowired
    private S3StorageService s3StorageService;

    @Value("${AWS-BUCKETNAME}")
    private String bucketName;

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

    @PostMapping(value = {"/restaurant/addDish"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseDto addDish(@RequestPart("restaurantId") String restaurantId,
                               @RequestPart("dish") Dish myDish,
                               @RequestPart("file") MultipartFile file)
    {
        ResponseDto response = new ResponseDto();

        try
        {
            if(myDish.getDishCategory() == null
                    || myDish.getDishName() == null
                    || myDish.getCost() < 1)
            {
                response.setMessage("The dish was not successfully saved because one of the fields are blank.");
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setTimestamp(new Date());
                response.setData(null);

                return response;
            }
            else
            {
                //Upload the image to Amazon S3 bucket
                String awsResult = s3StorageService.uploadFile(file);

                //Now get the restaurant that needs the menu updated
                Optional<Restaurant> myOptionalRestaurant = service.getRestaurantByRestaurantId(Integer.parseInt(restaurantId));
                if(myOptionalRestaurant.isEmpty())
                {
                    response.setMessage("Error! The dish could not be saved!");
                    response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
                    response.setTimestamp(new Date());
                    response.setData(null);
                }
                else
                {
                    Restaurant selectedRestaurant = myOptionalRestaurant.get();
                    //Append the dishes to the restaurant.
                    selectedRestaurant.setDishes(myDish);

                    service.updateRestaurant(myOptionalRestaurant.get());

                    response.setMessage("The dish was successfully saved.");
                    response.setStatus(HttpStatus.OK.value());
                    response.setTimestamp(new Date());
                    response.setData(myOptionalRestaurant.get().getDishes());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("An error has occurred! :" + e.getMessage());
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