package com.example.deliciousBee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.board.Restaurant;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByCategory(String category);

    //검색결과
    Page<Restaurant> findByNameContaining(
            String keyword, Pageable pageable);
}
