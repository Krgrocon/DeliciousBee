package com.example.deliciousBee.repository;

import com.example.deliciousBee.model.mypage.MyPage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MyPageRepository extends JpaRepository<MyPage, Long> {

	@Query("SELECT mp FROM MyPage mp JOIN mp.beeMember bm LEFT JOIN bm.review rl GROUP BY mp ORDER BY COUNT(rl) DESC")
	List<MyPage> findMyPagesOrderByReviewCountDesc();
	
	
	 @Query("SELECT mp FROM MyPage mp WHERE mp.beeMember.member_id = :memberId")
	    MyPage findMyPageWithVisitsByMemberId(@Param("memberId") String memberId);

	
}
