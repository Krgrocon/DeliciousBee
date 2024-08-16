package com.example.deliciousBee.model.board;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class RestaurantUpdateForm {
	
	private Long id;
    private String name;
    
    private String address;
    private Long phone_number;
    private String opening_hours;
    private String price_range;
    private String homepage_url;
    

    @Column(columnDefinition = "TEXT" , nullable = false)
    private String description;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

//    private Long image_id;
    @Column(nullable = false)
    private LocalDateTime create_at;
    
    @Column(nullable = false)
    private LocalDateTime updated_at;
    
    private Double average_rating;
    
    
	public static Restaurant toRestaurant(RestaurantUpdateForm restaurantUpdateForm) {
		Restaurant restaurant = new Restaurant();
		
		restaurant.setId(restaurantUpdateForm.getId());
		
		return restaurant;
	}
}
