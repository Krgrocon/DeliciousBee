package com.example.deliciousBee.controller.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.board.CategoryType;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.BeeUpdateForm;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.model.mypage.MyPageUpdateForm;
import com.example.deliciousBee.model.mypage.MyPageVisit;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.MyPageRepository;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.member.FollowService;
import com.example.deliciousBee.service.member.MyPageService;
import com.example.deliciousBee.service.review.ReviewService;
import com.example.deliciousBee.util.MemberFileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // 응답 html
@RequiredArgsConstructor
@RequestMapping("member")
public class MyPageController {

	@Value("${file.upload.path}") // 폴더를 찾아서 uploadPath한테 줌
	private String uploadPath; // c드라이브 어디에저장할지 지정

	private final MyPageRepository myPageRepository;
	private final MyPageService myPageService;
	private final BeeMemberService beeMemberService;
	private final HttpSession session;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ReviewService reviewService;
	private final FollowService followService;

	@Autowired
	private MemberFileService memberFileService; // fileStore 주입 받음.

	// **************마이페이지 이동******************
	@GetMapping("myPage")
	public String myPage(@AuthenticationPrincipal BeeMember loginMember,
			@RequestParam(name = "id", required = false) Long id, Model model) {
		MyPage myPage = null;

		// 로그인한 경우
		if (loginMember != null) {
			if (id == null) {
				myPage = myPageRepository.findMyPageWithVisitsByMemberId(loginMember.getMember_id());
			} else {
				myPage = myPageService.findById(id); // 다른 사용자의 마이페이지
			}
			model.addAttribute("loginMember", loginMember);
		} else {
			// 로그인하지 않은 경우
			if (id != null) {
				myPage = myPageService.findById(id); // 다른 사용자의 마이페이지만 조회 가능
			} else {
				return "redirect:/login"; // 자신의 마이페이지를 보려면 로그인 필요
			}
		}

		handleMyPageAccess(myPage, loginMember, model);

		return "member/myPage";
	}

	private void handleMyPageAccess(MyPage myPage, BeeMember loginMember, Model model) {
		myPageService.increaseHitCount(myPage.getId(), loginMember); // 조회수 증가
		myPageService.increaseVisitCount(myPage.getId(), loginMember); // 방문자 수 증가

		// 오늘 방문자 수 및 방문 기록 모델에 추가
		model.addAttribute("todayVisitCount", myPageService.getTodayVisitCount(myPage.getId()));
		List<MyPageVisit> visits = myPageService.getTodayMyPageVisits(myPage.getId()); // 오늘 방문 기록만 가져오도록 수정
		model.addAttribute("visitors", visits);
		List<MyPage> reviewCount = myPageRepository.findMyPagesOrderByReviewCountDesc(); // MyPageRepository를 직접 사용
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("myPage", myPage);
		// 로그인한 사용자의 팔로잉 목록 가져오기
		if (loginMember != null) {
	        List<BeeMember> followingList = followService.getFollowingList(loginMember.getMember_id());
	        model.addAttribute("followingList", followingList);
	    }
		
		 // 로그인한 사용자인지 확인
	    boolean isOwner = loginMember != null && loginMember.getMyPage().getId().equals(myPage.getId());
	    model.addAttribute("isOwner", isOwner); // isOwner 변수를 모델에 추가
	    
	    //리뷰 최신순
	    
		
	}
	@GetMapping("/random-review")
	@ResponseBody // JSON 형태로 응답
	public Map<String, String> getRandomReview() {
	    List<Review> allReviews = reviewService.findAllReviews();
	    Random random = new Random();
	    Map<String, String> result = new HashMap<>();

	    if (!allReviews.isEmpty()) {
	        List<Review> randomReviews = new ArrayList<>();
	        for (Review review : allReviews) {
	            if (review.getAttachedFile() != null && !review.getAttachedFile().isEmpty()) {
	                randomReviews.add(review);
	            }
	        }

	        if (!randomReviews.isEmpty()) {
	            int randomIndex = random.nextInt(randomReviews.size());
	            Review randomReview = randomReviews.get(randomIndex);
	            result.put("restaurantName", randomReview.getRestaurant().getName());
	            if (!randomReview.getAttachedFile().isEmpty()) { 
	                result.put("imageUrl", "/review/display?filename=" + randomReview.getAttachedFile().get(0).getSaved_filename());
	            }
	        }
	    }

	    return result;
	}

	
	// **********************마이페이지 수정하기 페이지 이동****************************
	@GetMapping("updateMyPage")
	public String goUpdateMyPage(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		MyPage myPage = loginMember.getMyPage(); // MyPage 객체를 가져옵니다.
		model.addAttribute("myPage", myPage); // myPage를 모델에 추가합니다.
		model.addAttribute("loginMember", loginMember);
		handleMyPageAccess(myPage, loginMember, model);
		return "member/updateMyPage";
	}

	// **********************마이페이지에서 수정하기*********************
	@PostMapping("updateMyPage")
	public String updateMyPage(@AuthenticationPrincipal BeeMember loginMember,
			@Validated @ModelAttribute("myPageUpdateForm") MyPageUpdateForm myPageUpdateForm,
		    BindingResult result,
			HttpServletRequest request, Model model) {

		if (result.hasErrors()) {
			return "member/myPage";
		}
		if (loginMember == null) {
			return "redirect:/member/login";
		}

		MyPage myPage = loginMember.getMyPage(); 
	    myPage.setIntroduce(myPageUpdateForm.getIntroduce()); // MyPageUpdateForm에서 introduce 값 사용
		    
		 myPageRepository.save(myPage); // MyPage 객체 저장
		return "redirect:/member/myPage";
	}


	// *******************사람들 마이페이지 리스트 **********************
	@GetMapping("myPageList")
	public String myPageList(@RequestParam(name = "searchText", defaultValue = "") String searchText,
			@PageableDefault(size = 10, sort = "id", direction = Direction.DESC) Pageable pageable, Model model) {

		Page<MyPage> myPageList = myPageService.findAll(pageable);
		model.addAttribute("myPageList", myPageList);
		int totalRecoardsCount = (int) myPageList.getTotalElements();

		List<Review> allReviews = reviewService.findAllReviews(); // 모든 리뷰 가져오기
		Random random = new Random();

		if (!allReviews.isEmpty()) {
			int randomIndex = random.nextInt(allReviews.size());
			Review randomReview = allReviews.get(randomIndex);
			model.addAttribute("randomReview", randomReview);
		}

		// 랜텀 카테고리
		Map<CategoryType, List<Review>> randomReviewsByCategory = new HashMap<>();
		for (CategoryType category : CategoryType.values()) {
			List<Review> randomReviews = reviewService.getRandomReviewsByCategory(category);
			randomReviewsByCategory.put(category, randomReviews);
		}
		model.addAttribute("randomReviewsByCategory", randomReviewsByCategory);

		// 리뷰 순 정리
		List<MyPage> reviewCount = myPageRepository.findMyPagesOrderByReviewCountDesc(); // MyPageRepository를 직접 사용
		model.addAttribute("reviewCount", reviewCount);
		return "member/MyPageList";

	}

}
