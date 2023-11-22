package inc.foodie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FoodieIncRestaurantServiceApplication
{
	public static void main(String[] args) {
		SpringApplication.run(FoodieIncRestaurantServiceApplication.class, args);
	}

}