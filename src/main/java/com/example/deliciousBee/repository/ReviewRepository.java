package com.example.deliciousBee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 레스토랑 ID를 통해서 review 가져오기 (페이징 처리)
	Page<Review> findByRestaurantId(Long restaurantId, Pageable pageable);

	// 평점순으로 정렬 (페이징 처리)
	Page<Review> findAllByRestaurant_IdOrderByRatingDesc(Long restaurantId, Pageable pageable);

	// 등록순으로 정렬 (페이징 처리)
	Page<Review> findAllByRestaurant_IdOrderByVisitDateDesc(Long restaurantId, Pageable pageable);

	// 좋아요순으로 정렬 (페이징 처리)
	Page<Review> findAllByRestaurant_IdOrderByLikeCountDesc(Long restaurantId, Pageable pageable);

//	// 랜덤 카테고리
//	@Query("SELECT r FROM Review r JOIN r.restaurant rest WHERE :category MEMBER OF rest.category")
//	List<Review> findByRestaurantCategoryContaining(@Param("category") CategoryType category);

	List<Review> findByRestaurant(Restaurant restaurant);

	List<Review> findByBeeMemberOrderByCreateDateDesc(BeeMember beeMember);

	// 방문 날짜 순
	List<Review> findByBeeMemberOrderByVisitDateDesc(BeeMember beeMember);
}