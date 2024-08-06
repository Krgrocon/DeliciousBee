package com.example.deliciousBee.repository;

import com.example.deliciousBee.model.review.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
