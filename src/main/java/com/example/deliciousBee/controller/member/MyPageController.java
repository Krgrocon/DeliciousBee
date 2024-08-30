package com.example.deliciousBee.controller.member;

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
import org.springframework.web.multipart.MultipartFile;

import com.example.deliciousBee.model.board.CategoryType;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.model.mypage.MyPageUpdateForm;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.repository.MyPageRepository;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.member.MyPageService;
import com.example.deliciousBee.service.review.ReviewService;
import com.example.deliciousBee.util.FileService;
import com.example.deliciousBee.util.MemberFileService;

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
	
	@Autowired
	private MemberFileService memberFileService; // fileStore 주입 받음.

	// **************마이페이지 이동******************
	@GetMapping("myPage")
	public String myPage(@AuthenticationPrincipal BeeMember loginMember,
			@RequestParam(name = "id", required = false) Long id, Model model) {
		MyPage myPage = null;

		if (loginMember != null) {
			// 로그인한 경우
			if (id == null) {
				myPage = loginMember.getMyPage(); // 자신의 마이페이지
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

		if (myPage == null) {
			return "redirect:/member/myPageList"; // 없는 마이페이지 접근 시 리스트로 리다이렉트
		}

		model.addAttribute("loginMember", loginMember);
		model.addAttribute("myPage", myPage); // MyPage 객체를 모델에 추가
		return "member/myPage";
	}

	// **********************마이페이지 수정하기 페이지 이동****************************
	@GetMapping("updateMyPage")
	public String goUpdateMyPage(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		MyPage myPage = loginMember.getMyPage(); // MyPage 객체를 가져옵니다.
		model.addAttribute("myPage", myPage); // myPage를 모델에 추가합니다.
		model.addAttribute("loginMember", loginMember);
		return "member/updateMyPage";
	}

	// **********************마이페이지에서 수정하기*********************
	@PostMapping("updateMyPage")
	public String updateMyPage(@AuthenticationPrincipal BeeMember loginMember,
			@Validated @ModelAttribute("myPageUpdateForm") MyPageUpdateForm myPageUpdateForm,
			@RequestParam(name = "profileImage", required = false) MultipartFile profileImage,
			@RequestParam(name = "mainImage", required = false) MultipartFile mainImage,
			@RequestParam(name = "backgroundImage", required = false) MultipartFile backgroundImage,
			BindingResult result, Model model) {

		if (result.hasErrors()) {
			return "member/myPage";
		}
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		MyPage myPage = loginMember.getMyPage(); // myPage 변수 초기화
	    myPage.setIntroduce(myPageUpdateForm.getIntroduce()); // 소개글 업데이트
	    
	    if (!profileImage.isEmpty()) {
	        MemberAttachedFile profileFile = memberFileService.saveFile(profileImage, "PROFILE");
	        profileFile.setMyPage(myPage);
	        myPageUpdateForm.setProfileImage(profileFile);
	        myPage.setProfileImage(profileFile);  // myPage 객체에 새로운 profileImage 설정
	    }
	    if (!mainImage.isEmpty()) {
	        MemberAttachedFile mainFile = memberFileService.saveFile(mainImage, "MAIN");
	        mainFile.setMyPage(myPage);
	        myPageUpdateForm.setMainImage(mainFile);
	        myPage.setMainImage(mainFile);  // myPage 객체에 새로운 mainImage 설정
	    }
	    if (!backgroundImage.isEmpty()) {
	        MemberAttachedFile backgroundFile = memberFileService.saveFile(backgroundImage, "BACKGROUND");
	        backgroundFile.setMyPage(myPage);
	        myPageUpdateForm.setBackgroundImage(backgroundFile);
	        myPage.setBackgroundImage(backgroundFile);  // myPage 객체에 새로운 backgroundImage 설정
	    }

	    myPageService.updateMyPage(myPage, null);  // 업데이트된 myPage 객체를 사용하여 저장

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
		
		//랜텀 카테고리
		Map<CategoryType, List<Review>> randomReviewsByCategory = new HashMap<>();
        for (CategoryType category : CategoryType.values()) {
            List<Review> randomReviews = reviewService.getRandomReviewsByCategory(category);
            randomReviewsByCategory.put(category, randomReviews);
        }
        model.addAttribute("randomReviewsByCategory", randomReviewsByCategory);
		
		
		//리뷰 순 정리
		 List<MyPage> reviewCount = myPageRepository.findMyPagesOrderByReviewCountDesc(); // MyPageRepository를 직접 사용
		 model.addAttribute("reviewCount", reviewCount);
		return "member/MyPageList";
		
		
		

	}


}
