package com.example.deliciousBee.controller.restaurant;

import com.example.deliciousBee.dto.member.SessionUser;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.board.RestaurantWriteForm;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import com.example.deliciousBee.service.review.ReviewService;
import com.example.deliciousBee.util.PageNavigator;
import com.example.deliciousBee.util.RestaurantFileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("restaurant")
public class RestaurantController {

	private String uploadPath = "C:\\upload\\";

	private final RestaurantService restaurantService;
	private final BeeMemberService beeMemberService;
	private final RestaurantFileService fileService;
	private final ReviewService reviewService;

	@GetMapping("newfile")
	public String newfile(@AuthenticationPrincipal BeeMember loginMember
			, Model model) {
		
		
		return "restaurant/newfile";
	}
	@GetMapping("rtwrite")
	public String rtwriteForm(@AuthenticationPrincipal BeeMember loginMember
			, Model model) {

		if(loginMember == null) {
			return "redirect:/member/login";
		}

		model.addAttribute("restaurantForm", new Restaurant());
		return "restaurant/rtwrite";
	}
	@PostMapping("restaurants")
	public String write(@Validated @ModelAttribute("restaurantForm") Restaurant restaurantWriteForm
			,BindingResult result
			,@AuthenticationPrincipal BeeMember loginMember
			,@RequestPart(name="file", required=false) MultipartFile[] files) {

		if(result.hasErrors()) {
			return "redirect:/";
		}

		BeeMember findMember = beeMemberService.findMemberById(loginMember.getMember_id());

		restaurantWriteForm.setMember(findMember);
		
		List<RestaurantAttachedFile> attachedFiles = new ArrayList<>();
		if (files != null && files.length > 0) { 
			System.out.println("gd");
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					log.info("file 들왓니>?..{}", file);
					RestaurantAttachedFile attachedFile = fileService.saveFile(file);
					attachedFile.setRestaurant(restaurantWriteForm);
					log.info("writefomr 들왓니>?..{}", restaurantWriteForm);
					attachedFiles.add(attachedFile);
					log.info("attachedFile 들왓니>?..{}", attachedFile);
				}
				System.out.println("gd");
			}
		}
		restaurantService.saveRestaurant(restaurantWriteForm, attachedFiles);
		return "redirect:/";
	}


	//검색
	@GetMapping("/search")
	public String searchRestaurants(@RequestParam(value = "keyword", required = false) String keyword,
									@PageableDefault(page = 0, size = 10) Pageable pageable,
									Model model) {

		Page<Restaurant> restaurants;
		if (keyword == null || keyword.isEmpty()) {
			// 검색어가 없는 경우 전체 레스토랑 목록 조회
			restaurants = restaurantService.findAll(pageable);
		} else {
			restaurants = restaurantService.searchByNameOrMenuName(keyword, pageable);
		}

		// PageNavigator 객체 생성 및 설정
		int countPerPage = pageable.getPageSize(); // 페이지당 글 목록 수
		int pagePerGroup = 5; // 그룹당 페이지 수
		int currentPage = pageable.getPageNumber() + 1; // 현재 페이지 (Pageable은 0부터 시작)
		int totalRecordsCount = (int) restaurants.getTotalElements(); // 전체 글 수
		int totalPageCount = restaurants.getTotalPages(); // 전체 페이지 수

		PageNavigator navi = new PageNavigator(countPerPage, pagePerGroup, currentPage, totalRecordsCount, totalPageCount);

		model.addAttribute("restaurants", restaurants);
		model.addAttribute("keyword", keyword);
		model.addAttribute("navi", navi); // navi 객체를 모델에 추가

		return "restaurant/rtlist";
	}
//	@GetMapping("/search")
//	public String searchRestaurants(@RequestParam(value = "keyword", required = false) String keyword,
//									@PageableDefault(page = 0, size = 10) Pageable pageable,
//									Model model) {
//
//		Page<Restaurant> restaurants;
//		if (keyword == null || keyword.isEmpty()) {
//			// 검색어가 없는 경우 전체 레스토랑 목록 조회
//			restaurants = restaurantService.findAll(pageable);restaurants = restaurantService.findAll(pageable);
//		} else {
//
//			restaurants = restaurantService.searchByNameOrMenuName(keyword, pageable); // 2번 방법
//		}
//
//		model.addAttribute("restaurants", restaurants);
//		model.addAttribute("keyword", keyword);
//		return "restaurant/rtlist"; // 검색 결과를 보여줄 뷰 이름
//	}


	@GetMapping("rtread/{restaurant_id}")
	public String read(@AuthenticationPrincipal BeeMember loginMember
			,@PathVariable("restaurant_id") Long restaurant_id
			,Model model) {
		if(loginMember == null) {
			return "redirect:/member/login";
		}

		// 레스토랑 정보 가져오기
		Restaurant restaurant = restaurantService.findRestaurant(restaurant_id);
		if(restaurant == null) {
			return "redirect:/shop/index";
		}
		model.addAttribute("restaurant", restaurant);

		// 리뷰 정보 가져오기
		String memberId = loginMember.getMember_id();
		List<Review> reviewsByRestaurant = reviewService.getReviewsByRestaurantIdWithFiles(restaurant_id, memberId);
		log.info("************** allreview:{}", reviewsByRestaurant);
		model.addAttribute("reviewsByRestaurant", reviewsByRestaurant);
		return "restaurant/rtread";
	}

	@GetMapping("rtdelete")
	public String remove(@AuthenticationPrincipal BeeMember loginMember,
						 @RequestParam(name="id", required = false) Long id) {

		Restaurant restaurant = restaurantService.findRestaurant(id);

		if(restaurant == null || !restaurant.getMember().getMember_id().equals(loginMember.getMember_id())) {
			return "redirect:/";
		}

		restaurantService.deleteRestaurant(restaurant.getId());

		return "redirect:/";
	}

	@GetMapping("rtupdate")
	public String rtUpdate(@AuthenticationPrincipal BeeMember loginMember,
						   @RequestParam(name = "id") Long id, Model model) {
		log.info("{}", loginMember);
		if(loginMember == null) {
			return "redirect:/member/login";
		}
		Restaurant findRestaurant = restaurantService.findRestaurant(id);
		if(findRestaurant == null || !findRestaurant.getMember().getMember_id().equals(loginMember.getMember_id())) {

			return "redirect:/shop/index";
		}

		model.addAttribute("restaurantForm", findRestaurant);
		return "restaurant/rtupdate";
	}


	@PostMapping("rtupdates")
	public String update(@Validated @ModelAttribute("restaurantForm") Restaurant update
			,BindingResult result
	) {

		if(result.hasErrors()) {
			return "restaurant/rtupdate";
		}
		//수정
		Restaurant updateRestaurant = Restaurant.toRestaurant(update);

		restaurantService.updateRestaurant(updateRestaurant);

		//수정되면
		return "redirect:/";
	}
	
	@GetMapping("/display")
	public ResponseEntity<Resource> display(@RequestParam("filename") String filename) {
		String folder = "";
		Resource resource = new FileSystemResource(uploadPath + folder + filename);
		if (!resource.exists()) {
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);
		}
		HttpHeaders header = new HttpHeaders();
		Path filePath = null;
		try {
			filePath = Paths.get(uploadPath + folder + filename);
			header.add("Content-type", Files.probeContentType(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	
//	@GetMapping("/restaurants")
//	public String getRestaurants(Pageable pageable) { 
//	    Page<Restaurant> restaurants = restaurantService.findByNameContaining(keyword, pageable); 
	    
	@GetMapping("rtlist")
	public String restdList(//@RequestParam(name="id") Model model  
							//@PageableDefault(page = 0, size = 10)
							//Pageable pageable,
							//String keyword
			){
//
//		/*검색기능-3*/
//		Page<Restaurant> list = null;
//
//		/*searchKeyword = 검색하는 단어*/
//		if(keyword == null){
//			list = restaurantService.restaurnatList(pageable); //기존의 리스트보여줌
//		}else{
////			list = restaurantService.restaurantSearchList(keyword, pageable); //검색리스트반환
//		}
//
//		int nowPage = list.getPageable().getPageNumber() + 1; //pageable에서 넘어온 현재페이지를 가지고올수있다 * 0부터시작하니까 +1
//		int startPage = Math.max(nowPage - 4, 1); //매개변수로 들어온 두 값을 비교해서 큰값을 반환
//		int endPage = Math.min(nowPage + 5, list.getTotalPages());
//
//		//BoardService에서 만들어준 boardList가 반환되는데, list라는 이름으로 받아서 넘기겠다는 뜻
//		model.addAttribute("list" , list);
//		model.addAttribute("nowPage", nowPage);
//		model.addAttribute("startPage", startPage);
//		model.addAttribute("endPage", endPage);
//
		return "restaurant/rtlist";
	}


//		@GetMapping("/category")
//		public String filterByCategory(@RequestParam("category"ㅋ) String category, Model model) {
//		    List<Restaurant> restaurants = restaurantService.findByCategory(category);
//		    model.addAttribute("restaurants", restaurants);
//		    model.addAttribute("selectedCategory", category);
//		    return "/category/korean"; // 목록을 보여줄 페이지
//		}



}
