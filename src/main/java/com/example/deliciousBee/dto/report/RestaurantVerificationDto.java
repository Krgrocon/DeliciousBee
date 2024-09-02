package com.example.deliciousBee.dto.report;

import com.example.deliciousBee.model.board.Restaurant;
import lombok.Data;

@Data
public class RestaurantVerificationDto {
    private Long id;
    private String name;
    private String address;;
    private String description;
    private Long restaurantId; // Restaurant 엔티티의 id를 저장할 필드



    public RestaurantVerificationDto(Restaurant restaurant){
        this.id =restaurant.getId();
        this.name =restaurant.getName();
        this.address=restaurant.getAddress();
        this.restaurantId = restaurant.getId();
    }
}