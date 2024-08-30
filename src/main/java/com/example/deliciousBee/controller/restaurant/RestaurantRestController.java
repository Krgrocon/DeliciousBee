
package com.example.deliciousBee.controller.restaurant;

import com.example.deliciousBee.dto.restaurant.RestaurantDto;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/restaurants")
public class RestaurantRestController {

    private final RestaurantService restaurantService;
    private final PagedResourcesAssembler<RestaurantDto> assembler;

    @Autowired
    public RestaurantRestController(RestaurantService restaurantService, PagedResourcesAssembler<RestaurantDto> assembler) {
        this.restaurantService = restaurantService;
        this.assembler = assembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<RestaurantDto>>> getRestaurants(
            @RequestParam(value = "keyword", required = false) String keyword,
            Pageable pageable,
            @RequestParam(value = "sortBy", required = false, defaultValue = "default") String sortBy,
            @RequestParam(value = "latitude", required = false) Double userLatitude,
            @RequestParam(value = "longitude", required = false) Double userLongitude) {

        Page<RestaurantDto> restaurants;

        if (keyword == null || keyword.isEmpty()) {
            restaurants = restaurantService.searchRestaurants(null, pageable, sortBy, userLatitude, userLongitude);
        } else {
            restaurants = restaurantService.searchRestaurants(keyword, pageable, sortBy, userLatitude, userLongitude);
        }

        return ResponseEntity.ok(assembler.toModel(restaurants));
    }
}