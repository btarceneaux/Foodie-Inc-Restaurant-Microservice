package inc.foodie.bean;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Restaurant
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int restaurantId;
    private String restaurantName;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Dish> dishes = new ArrayList<>();

    public Restaurant()
    {
    }

    public int getRestaurantId()
    {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId)
    {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName()
    {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName)
    {
        this.restaurantName = restaurantName;
    }

    public List<Dish> getDishes()
    {
        return dishes;
    }

    public void setDishes(Dish myDish)
    {
        this.dishes.add(myDish);
    }

}