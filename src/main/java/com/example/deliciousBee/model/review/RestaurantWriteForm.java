package com.example.deliciousBee.model.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RestaurantWriteForm {
    private String name;
    
    private String address;
    private Long phone_number;
    private String opening_hours;
    private String price_range;
    private String homepage_url;
    private String description;
    private Double longitude;
    private Double latitude;
    private LocalDateTime create_at;
    private LocalDateTime updated_at;
    private Double average_rating;
    private Long review_count;
    
	public static Restaurant toRestaurant(RestaurantWriteForm rwf) {
		Restaurant restaurant = new Restaurant();
		restaurant.setName(rwf.getName());
		restaurant.setAddress(rwf.getAddress());
		restaurant.setPhone_number(rwf.getPhone_number());
		restaurant.setOpening_hours(rwf.getOpening_hours());
		restaurant.setPrice_range(rwf.getPrice_range());
		restaurant.setHomepage_url(rwf.getHomepage_url());
		restaurant.setDescription(rwf.getDescription());
		restaurant.setLongitude(rwf.getLongitude());
		restaurant.setLatitude(rwf.getLatitude());
		restaurant.setCreate_at(rwf.getCreate_at());
		restaurant.setUpdated_at(rwf.getUpdated_at());
		restaurant.setAverage_rating(rwf.getAverage_rating());
		restaurant.setReview_count(rwf.getReview_count());
		
		return restaurant;
	}

}
