package com.example.deliciousBee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.board.Restaurant;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCategory(String category);


    @Query(value = "SELECT * FROM restaurant ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Restaurant> findRandom5Restaurants();
    //검색결과
    Page<Restaurant> findByNameContaining(
            String keyword, Pageable pageable);
}
