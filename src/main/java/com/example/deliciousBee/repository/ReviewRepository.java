package com.example.deliciousBee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.board.CategoryType;
import com.example.deliciousBee.model.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{

	// 레스토랑 ID를 통해서 review 가져오기
	List<Review> findByRestaurantId(Long restaurantId);

	// 평점순으로 정렬
	List<Review> findAllByRestaurant_IdOrderByRatingDesc(Long restaurantId);
	// 등록순으로 정렬
	List<Review>  findAllByRestaurant_IdOrderByVisitDateDesc(Long restaurantId);
	// 좋아요순으로 정렬
	List<Review> findAllByRestaurant_IdOrderByLikeCountDesc(Long restaurantId);
	// 랜덤 카테고리
	List<Review> findByRestaurant_Category(CategoryType category);


}
