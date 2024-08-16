package com.example.deliciousBee.service.restaurant;


import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.repository.RestaurantRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {


    private final RestaurantRepository restaurantRepository;

    public void saveRestaurant(Restaurant restaurant) {
        restaurantRepository.save(restaurant);
    }
    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public Restaurant findRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id).get();
        return restaurant;
    }

//    public Restaurant findRestaurant(Long id) {
//        return restaurantRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid restaurant Id:" + id));
//    }

    @Transactional
    public void updateRestaurant(Restaurant updateRestaurant) {
        Restaurant findRestaurant = findRestaurant(updateRestaurant.getId());

        findRestaurant.setName(updateRestaurant.getName());
        findRestaurant.setAddress(updateRestaurant.getAddress());
        findRestaurant.setPhone_number(updateRestaurant.getPhone_number());
        findRestaurant.setOpening_hours(updateRestaurant.getOpening_hours());
        findRestaurant.setPrice_range(updateRestaurant.getPrice_range());
        findRestaurant.setHomepage_url(updateRestaurant.getHomepage_url());
        findRestaurant.setDescription(updateRestaurant.getDescription());
        findRestaurant.setLongitude(updateRestaurant.getLongitude());
        findRestaurant.setLatitude(updateRestaurant.getLatitude());
        findRestaurant.setUpdated_at(updateRestaurant.getUpdated_at());
        findRestaurant.setCategory(updateRestaurant.getCategory());

        restaurantRepository.save(findRestaurant);
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }

    // 카테고리
    public List<Restaurant> findByCategory(String category) {
        return restaurantRepository.findByCategory(category);
    }

    //페이지
    public Page<Restaurant> restaurnatList(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

//    public Page<Restaurant> restaurantSearchList(String keyword, Pageable pageable) {
//        return restaurantRepository.findByNameContaining(
//                keyword, pageable);
//    }

}