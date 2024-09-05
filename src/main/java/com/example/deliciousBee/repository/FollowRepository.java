package com.example.deliciousBee.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.deliciousBee.model.member.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	@Modifying // DELETE 쿼리임을 나타냄
    @Query("DELETE FROM Follow f WHERE f.follower.member_id = :followerId AND f.following.member_id = :followingId") // 쿼리 명시
    void deleteByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);

	 @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Follow f WHERE f.follower.member_id = :followerId AND f.following.member_id = :followingId") // 쿼리 명시
	    boolean existsByFollowerIdAndFollowingId(@Param("followerId") String followerId, @Param("followingId") String followingId);
	 
	 @Query("SELECT f FROM Follow f WHERE f.follower.member_id = :followerId")
	    List<Follow> findByFollowerMemberId(@Param("followerId") String followerId); 
}