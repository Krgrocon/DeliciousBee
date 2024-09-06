package com.example.deliciousBee.repository;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.model.mypage.MyPageVisit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MyPageVisitRepository extends JpaRepository<MyPageVisit, Long> {

	List<MyPageVisit> findByMyPageIdAndVisitDate(Long myPageId, LocalDate visitDate); // 특정 날짜의 방문 기록 조회

	long countDistinctVisitorByMyPageIdAndVisitDate(Long myPageId, LocalDate visitDate); // 특정 날짜의 고유 방문자 수 계산

	boolean existsByMyPageAndVisitorAndVisitDate(MyPage myPage, BeeMember visitor, LocalDate visitDate);

	void deleteByVisitor(BeeMember loginMember);
}