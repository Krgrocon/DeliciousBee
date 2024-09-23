package com.example.deliciousBee.controller.restaurant;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.keyWord.KeyWord;
import com.example.deliciousBee.model.keyWord.KeywordCategory;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.service.keyWord.ReviewKeyWordService;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import com.example.deliciousBee.service.review.ReviewService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("restaurant")
public class RestaurantController {

	@Value("${spring.cloud.gcp.storage.bucket}")
	private String bucketName;

	private String uploadPath = "C:\\upload\\";

	private final RestaurantService restaurantService;
	private final ReviewService reviewService;
	private final ReviewKeyWordService reviewKeyWordService;

	@GetMapping("newfile")
	public String newfile(@AuthenticationPrincipal BeeMember loginMember, Model model) {

		return "restaurant/newfile";
	}

	@GetMapping("rtwrite")
	public String rtwriteForm(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		model.addAttribute("restaurantForm", new Restaurant());
		return "restaurant/rtwrite";
	}
//	@PostMapping("restaurants")
//	public String write(@Validated @ModelAttribute("restaurantForm") Restaurant restaurantform
//			,BindingResult result
//			,@AuthenticationPrincipal BeeMember loginMember
//			,@RequestPart(name="file", required=false) MultipartFile[] files) {
//
//		if(result.hasErrors()) {
//			return "redirect:/";
//		}
//
//		BeeMember findMember = beeMemberService.findMemberById(loginMember.getMember_id());
//
//		restaurantform.setMember(findMember);
//
//		List<RestaurantAttachedFile> attachedFiles = new ArrayList<>();
//		if (files != null && files.length > 0) {
//			System.out.println("gd");
//			for (MultipartFile file : files) {
//				if (!file.isEmpty()) {
//					log.info("file 들왓니>?..{}", file);
//					RestaurantAttachedFile attachedFile = fileService.saveFile(file);
//					attachedFile.setRestaurant(restaurantform);
//					log.info("writefomr 들왓니>?..{}", restaurantform);
//					attachedFiles.add(attachedFile);
//					log.info("attachedFile 들왓니>?..{}", attachedFile);
//				}
//				System.out.println("gd");
//			}
//		}
//		restaurantService.saveRestaurant(restaurantform, attachedFiles);
//		return "redirect:/";
//	}

	// 검색
	@GetMapping("/search")
	public String searchRestaurants(@RequestParam(value = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10) Pageable pageable, Model model) {

//		Page<Restaurant> restaurants;
//		if (keyword == null || keyword.isEmpty()) {
//			// 검색어가 없는 경우 전체 레스토랑 목록 조회
//			restaurants = restaurantService.findAll(pageable);
//		} else {
//			restaurants = restaurantService.searchByNameOrMenuName(keyword, pageable);
//		}
//
//		// PageNavigator 객체 생성 및 설정
//		int countPerPage = pageable.getPageSize(); // 페이지당 글 목록 수
//		int pagePerGroup = 5; // 그룹당 페이지 수
//		int currentPage = pageable.getPageNumber() + 1; // 현재 페이지 (Pageable은 0부터 시작)
//		int totalRecordsCount = (int) restaurants.getTotalElements(); // 전체 글 수
//		int totalPageCount = restaurants.getTotalPages(); // 전체 페이지 수
//
//		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, currentPage, totalRecordsCount, totalPageCount);
//
//		model.addAttribute("restaurants", restaurants);
		model.addAttribute("keyword", keyword);
//		model.addAttribute("navi", navi); // navi 객체를 모델에 추가

		return "restaurant/rtlist";
	}

	@GetMapping("/rtread/{restaurant_id}")
	public String read(@AuthenticationPrincipal BeeMember loginMember,
			@PathVariable("restaurant_id") Long restaurant_id, Model model,
			@RequestParam(name = "page", defaultValue = "0") int page) { // 페이징 처리를 위한 page 파라미터 추가

		if (loginMember == null) {
			return "redirect:/member/login";
		}

		// 레스토랑 정보 가져오기
		Restaurant restaurant = restaurantService.findRestaurant(restaurant_id);
		if (restaurant == null) {
			return "redirect:/shop/index";
		}
		model.addAttribute("restaurant", restaurant);

		// 리뷰 정보 가져오기
		String memberId = loginMember.getMember_id();
		Pageable pageable = PageRequest.of(page, 5); // 페이지당 5개 리뷰로 설정
		Page<Review> reviewsByRestaurant = reviewService.getReviewsByRestaurantIdWithFiles(restaurant_id, memberId, pageable);
		log.info("reviewsByRestaurant:{}", reviewsByRestaurant);
		model.addAttribute("reviewsByRestaurant", reviewsByRestaurant.getContent()); 
		model.addAttribute("currentPage", page); 
		model.addAttribute("totalPages", reviewsByRestaurant.getTotalPages()); 

		// 카테고리 가져오기
		Map<KeywordCategory, List<KeyWord>> keywordsByCategory = reviewKeyWordService.getKeywordsByCategory();
		model.addAttribute("keywordsByCategory", keywordsByCategory);
		return "restaurant/rtread";
	}

	@GetMapping("rtdelete")
	public String remove(@AuthenticationPrincipal BeeMember loginMember,
			@RequestParam(name = "id", required = false) Long id) {

		Restaurant restaurant = restaurantService.findRestaurant(id);

		if (restaurant == null || !restaurant.getMember().getMember_id().equals(loginMember.getMember_id())) {
			return "redirect:/";
		}

		restaurantService.deleteRestaurant(restaurant.getId());

		return "redirect:/";
	}

	@GetMapping("rtupdate")
	public String rtUpdate(@AuthenticationPrincipal BeeMember loginMember, @RequestParam(name = "id") Long id,
			Model model) {
		log.info("{}", loginMember);
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		Restaurant findRestaurant = restaurantService.findRestaurant(id);
//		if(findRestaurant == null || !findRestaurant.getMember().getMember_id().equals(loginMember.getMember_id())) {
//
//			return "redirect:/shop/index";
//		}

		model.addAttribute("restaurantForm", findRestaurant);
		return "restaurant/rtupdate";
	}

//	@PostMapping("rtupdates")
//	public String update(@Validated @ModelAttribute("restaurantForm") Restaurant update
//			,BindingResult result
//	) {
//
//		if(result.hasErrors()) {
//			return "restaurant/rtupdate";
//		}
//		//수정
//		Restaurant updateRestaurant = Restaurant.toRestaurant(update);
//
//		restaurantService.updateRestaurant(updateRestaurant);
//
//		//수정되면
//		return "redirect:/";
//	}

	@GetMapping("/display")
	public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
		try {
			// Google Cloud Storage 키 파일 설정
			String keyFileName = "deliciousbee-acb114448e3c.json"; // GCP 서비스 계정 키 파일명
			InputStream keyFile = getClass().getResourceAsStream("/" + keyFileName);

			// Google Cloud Storage 클라이언트 생성
			Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(keyFile)).build()
					.getService();

			// 파일을 GCS에서 가져오기
			Blob blob = storage.get(bucketName, filename);

			if (blob == null || !blob.exists()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			// Blob의 데이터를 ByteArrayResource로 변환
			Resource resource = new ByteArrayResource(blob.getContent());

			// 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", blob.getContentType());

			return new ResponseEntity<>(resource, headers, HttpStatus.OK);

		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
