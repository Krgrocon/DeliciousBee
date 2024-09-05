package com.example.deliciousBee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.deliciousBee.model.like.ReviewLike;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.review.Review;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long>{
	
	// 좋아요 존재 여부 확인
	boolean existsByBeeMemberAndReview(BeeMember beeMember, Review review);
    // 좋아요 삭제
	void deleteByBeeMemberAndReview(BeeMember beeMember, Review review);
}
