package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{


}
