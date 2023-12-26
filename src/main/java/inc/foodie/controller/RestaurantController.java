package inc.foodie.controller;

import inc.foodie.bean.Dish;
import inc.foodie.bean.Restaurant;
import inc.foodie.dto.ResponseDto;
import inc.foodie.service.RestaurantService;
import inc.foodie.service.S3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
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
                String myFileURL = "https://s3.us-west-2.amazonaws.com/foodie.inc.fileupload/" + s3StorageService.uploadFile(file);
                myDish.setImageURL(myFileURL);

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
    public ResponseDto createRestaurant(@RequestBody String restaurantName)
    {
        ResponseDto response = new ResponseDto();

        if(restaurantName == null)
        {
            response.setMessage("The restaurant was not successfully saved because one of the fields are blank.");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setTimestamp(new Date());
            response.setData(null);

            return response;
        }
        Restaurant tempRestaurant = new Restaurant();
        tempRestaurant.setRestaurantName(restaurantName);

        Restaurant savedRestaurant = service.createRestaurant(tempRestaurant);

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

    @PutMapping("/restaurant/updateRestaurant/{restaurantId}")
    public ResponseDto updateRestaurantInfo(@PathVariable int restaurantId, @RequestBody String newRestaurantName)
    {
        ResponseDto response = new ResponseDto();

        //First we need to get the restaurant that needs to be changed.
        Optional<Restaurant> optionalRestaurant = service.getRestaurantByRestaurantId(restaurantId);

        if(optionalRestaurant.isPresent())
        {
            Restaurant myRestaurant = optionalRestaurant.get();

            myRestaurant.setRestaurantName(newRestaurantName);

            //Finally, we can sync everything to the database
            Restaurant updatedRestaurant  = service.updateRestaurant(myRestaurant);

            response.setMessage("The restaurant was successfully updated.");
            response.setStatus(HttpStatus.OK.value());
            response.setTimestamp(new Date());
            response.setData(updatedRestaurant);
        }
        else
        {
            response.setMessage("Error! The restaurant was not successfully updated.");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.setTimestamp(new Date());
            response.setData(null);
        }

        return response;
    }

    @PutMapping("/restaurant/updateDish/{restaurantId}")
    public ResponseDto updateRestaurantDish(@PathVariable int restaurantId, @RequestBody Dish myDish)
    {
        ResponseDto response = new ResponseDto();

        //First we need to get the restaurant that needs to be changed.
        Optional<Restaurant> optionalRestaurant = service.getRestaurantByRestaurantId(restaurantId);

        //Find the dish that needs to be updated and update it.
        if(optionalRestaurant.isPresent())
        {
            Restaurant myRestaurant = optionalRestaurant.get();
            int dishIndex = 0;
            for(Dish tempDish : myRestaurant.getDishes())
            {
                if(tempDish.getDishId() == myDish.getDishId())
                {
                    myRestaurant.getDishes().get(dishIndex).setDishName(myDish.getDishName());
                    myRestaurant.getDishes().get(dishIndex).setDishCategory(myDish.getDishCategory());
                    myRestaurant.getDishes().get(dishIndex).setCost(myDish.getCost());

                    break;
                }
                dishIndex++;
            }

            //Finally, we can sync everything to the database
            Restaurant updatedRestaurant  = service.updateRestaurant(myRestaurant);

            response.setMessage("The dish was successfully updated.");
            response.setStatus(HttpStatus.OK.value());
            response.setTimestamp(new Date());
            response.setData(updatedRestaurant);
        }

        return response;
    }

    @DeleteMapping("/restaurant/deleteDish/{restaurantId}/{dishId}")
    public ResponseDto updateRestaurantDishQuantities(@PathVariable int restaurantId, @PathVariable int dishId)
    {
        ResponseDto response = new ResponseDto();

        //First we need to get the restaurant that needs to be changed.

        Optional<Restaurant> optionalRestaurant = service.getRestaurantByRestaurantId(restaurantId);
        if(optionalRestaurant.isPresent())
        {
            Restaurant myRestaurant = optionalRestaurant.get();

            String dishImageFileName = myRestaurant.getDishes().get(dishId).getImageURL();

            //Remove the path part of the dishImageFileName
            String fileName = dishImageFileName.replaceAll("https://s3.us-west-2.amazonaws.com/foodie.inc.fileupload/", "");

            //First, let's remove the image from the S3 bucket
            s3StorageService.deleteFile(fileName);

            //Next we need to remove the item from the array list
            myRestaurant.getDishes().remove(dishId);

            //Finally, we can sync everything to the database
            Restaurant updatedRestaurant  = service.updateRestaurant(myRestaurant);

            response.setMessage("The dish was successfully removed.");
            response.setStatus(HttpStatus.OK.value());
            response.setTimestamp(new Date());
            response.setData(updatedRestaurant);


        }
        else
        {
            response.setMessage("The dish was not removed.");
            response.setStatus(HttpStatus.EXPECTATION_FAILED.value());
            response.setTimestamp(new Date());
            response.setData(null);
        }

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