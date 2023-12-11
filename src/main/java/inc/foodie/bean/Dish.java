package inc.foodie.bean;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Dish
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dishId;
    private String dishName;
    private String dishCategory;
    private double cost;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ImageModel> dishImages = new HashSet<>();

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

    public Set<ImageModel> getDishImages()
    {
        return dishImages;
    }

    public void setDishImages(Set<ImageModel> dishImages)
    {
        for(ImageModel image : dishImages)
        {
            this.dishImages.add(image);
        }
    }
}