package com.example.deliciousBee.repository;

import java.util.List;


import com.example.deliciousBee.model.file.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.deliciousBee.model.review.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<AttachedFile, Long>{

	// 리뷰아이디를 통해서 파일가져오기
	@Query("SELECT f FROM AttachedFile f WHERE f.review.id = :reviewId")
	List<AttachedFile> findFilesByReviewId(@Param("reviewId") Long reviewId);

	// 리뷰 아이디로 첨부파일 삭제
	void deleteByReviewId(Long reviewId);

	AttachedFile findByReview(Review review);

	// 파일 아이디로 첨부파일삭제
	void deleteById(Long attachedFileId);
	
	// 리뷰로 첨부파일 찾기
	List<AttachedFile> findAllByReview(Review review);

}
