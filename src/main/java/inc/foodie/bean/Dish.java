package inc.foodie.bean;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Dish
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dishId;
    private String dishName;
    private String dishCategory;
    private double cost;

    public Dish()
    {
    }

    public int getDishId()
    {
        return dishId;
    }

    public void setDishId(int dishId)
    {
        this.dishId = dishId;
    }

    public String getDishName()
    {
        return dishName;
    }

    public void setDishName(String dishName)
    {
        this.dishName = dishName;
    }

    public String getDishCategory()
    {
        return dishCategory;
    }

    public void setDishCategory(String dishCategory)
    {
        this.dishCategory = dishCategory;
    }

    public double getCost()
    {
        return cost;
    }

    public void setCost(double cost)
    {
        this.cost = cost;
    }
}