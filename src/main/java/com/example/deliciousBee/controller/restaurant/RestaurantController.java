package com.example.deliciousBee.controller.restaurant;

import com.example.deliciousBee.dto.member.SessionUser;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.board.RestaurantWriteForm;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("restaurant")
public class RestaurantController {

	private final RestaurantService restaurantService;
	private final BeeMemberService beeMemberService;

	@GetMapping("rtwrite")
	public String rtwriteForm(@AuthenticationPrincipal BeeMember loginMember
			, Model model) {

		if(loginMember == null) {
			return "redirect:/member/LoginForm";
		}

		model.addAttribute("restaurantForm", new Restaurant());
		return "restaurant/rtwrite";
	}
	@PostMapping("restaurants")
	public String write(@Validated @ModelAttribute("restaurantForm") Restaurant restaurantWriteForm
			,BindingResult result
			,@AuthenticationPrincipal BeeMember loginMember) {

		if(result.hasErrors()) {
			return "redirect:/";
		}

		BeeMember findMember = beeMemberService.findMemberById(loginMember.getMember_id());

		restaurantWriteForm.setMember(findMember);
		restaurantService.saveRestaurant(restaurantWriteForm);
		return "redirect:/";
	}

	@GetMapping("rtread")
	public String read(@AuthenticationPrincipal BeeMember loginMember,
					   @RequestParam(name = "id") Long id, Model model) {

		if(loginMember == null) {
			return "redirect:/member/BeeLoginForm";
		}

		Restaurant restaurant = restaurantService.findRestaurant(id);

		if(restaurant == null) {
			return "redirect:/shop/index";
		}

		model.addAttribute("restaurant", restaurant);
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
			return "redirect:/member/loginForm";
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

//	@GetMapping("rtlist")
//	public String restdList(Model model,
//							@PageableDefault(page = 0, size = 10)
//							Pageable pageable,
//							String keyword){
//
//		/*검색기능-3*/
//		Page<Restaurant> list = null;
//
//		/*searchKeyword = 검색하는 단어*/
//		if(keyword == null){
//			list = restaurantService.restaurnatList(pageable); //기존의 리스트보여줌
//		}else{
//			list = restaurantService.restaurantSearchList(keyword, pageable); //검색리스트반환
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
//		return "redirect:/";
//	}


//		@GetMapping("/category")
//		public String filterByCategory(@RequestParam("category") String category, Model model) {
//		    List<Restaurant> restaurants = restaurantService.findByCategory(category);
//		    model.addAttribute("restaurants", restaurants);
//		    model.addAttribute("selectedCategory", category);
//		    return "/category/korean"; // 목록을 보여줄 페이지
//		}



}
