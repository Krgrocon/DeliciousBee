package com.example.deliciousBee.service.report;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.deliciousBee.dto.report.ReportDto;
import com.example.deliciousBee.repository.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.report.Report;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.ReportRepository;
import com.example.deliciousBee.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
	
	private final ReportRepository reportRepository;
	private final ReviewRepository reviewRepository;
	private final FileRepository fileRepository;


	public Optional<Report> getReportById(Long reportId) {
		return reportRepository.findById(reportId);
	}

	// 신고 보내는 기능
	public boolean sendReport(Long reviewId, Report report) {
		try {
			Review findReview = reviewRepository.findById(reviewId).get();
			
			// 리뷰 유무 체크
			if(findReview == null) {
				return false;
			}

			// 신고 등록
			report.setReview(findReview);
			reportRepository.save(report);
			return true;
		} catch (Exception e) {
			log.error("신고 서버 제출에 실패하였습니다.", e);
			return false;
		}
	}

	public List<ReportDto> getAllReportDtos() {
		List<Report> reports = reportRepository.findAll();
		return reports.stream()
				.map(this::convertToDto)
				.collect(Collectors.toList());
	}

	private ReportDto convertToDto(Report report) {
		ReportDto dto = new ReportDto();
		dto.setId(report.getId());
		dto.setReason(report.getReason());
		dto.setReportDate(report.getReportDate());


		if (report.getReview() != null) {
			dto.setReviewId(report.getReview().getId());
		}
		// 필요한 경우, Review, Restaurant, BeeMember 정보를 DTO에 추가합니다.
		// 예시:
		// dto.setReviewId(report.getReview().getId());
		// dto.setRestaurantName(report.getRestaurant().getName());
		// dto.setBeeMemberId(report.getBeeMember().getMember_id());

		return dto;
	}
	@Transactional
	public void deleteReport(Long reportId) {
		reportRepository.deleteById(reportId);
	}


	// 리포트 승인 (삭제) 메서드로 변경
	@Transactional
	public void approveReport(Long reportId) throws Exception {
		Report report = reportRepository.findById(reportId)
				.orElseThrow(() -> new Exception("리포트를 찾을 수 없습니다."));
		reportRepository.delete(report);
	}

	// 리뷰 삭제 시 연관된 모든 리포트도 함께 삭제
	@Transactional
	public void deleteReview(Long reviewId) throws Exception {
		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new Exception("리뷰를 찾을 수 없습니다."));

		// 해당 리뷰와 연관된 모든 리포트 조회
		List<Report> reports = reportRepository.findByReviewId(reviewId);
		reportRepository.deleteAll(reports);

		try {
			if (reportRepository.existsById(reviewId)) {
				reportRepository.deleteById(reviewId);
			}
			fileRepository.deleteByReviewId(reviewId);
			reviewRepository.deleteById(reviewId);

		} catch (Exception e) {
			log.error("Error deleting review with id: " + reviewId, e);
		}
	}
}
