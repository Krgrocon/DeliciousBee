package com.example.deliciousBee.repository;

import java.util.List;
import java.util.Optional;

import com.example.deliciousBee.dto.report.ReportDto;
import com.example.deliciousBee.model.member.BeeMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.deliciousBee.model.report.Report;

public interface ReportRepository extends JpaRepository<Report, Long>{
	
	@Query("SELECT r.review.id FROM Report r WHERE r.beeMember.id = :memberId")
	List<Long> findReportedReviewId(@Param("memberId")String memberId);

	List<Report> findByReviewId(Long reviewId);

	Optional<ReportDto> findReportDtoById(Long Id);

}
