package com.example.deliciousBee.controller;

import com.example.deliciousBee.model.review.Restaurant;
import com.example.deliciousBee.model.review.RestaurantWriteForm;
import com.example.deliciousBee.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("restaurant")
public class RestraurantController {

		private final RestaurantService restaurantService;
		
		@GetMapping("rtwrite")
		public String rtwriteForm(Model model) {
			model.addAttribute("restaurantForm", new RestaurantWriteForm());
			return "restaurant/rtwrite";
		}
		@PostMapping("restaurants")
		public String write(@Validated @ModelAttribute("restaurantForm") RestaurantWriteForm restaurantWriteForm
						   ,BindingResult result) {
			System.out.println("아아");
			
			if(result.hasErrors()) {
				log.info("오류뜨니?");
				return "redirect:/";
			}
			System.out.println("gg");
			Restaurant restaurant = RestaurantWriteForm.toRestaurant(restaurantWriteForm);
			System.out.println(restaurant);
			System.out.println("dd");
			 restaurantService.saveRestaurant(restaurant);
			return "redirect:/";
		}
		
		@GetMapping("rtread")
		public String read(// @SessionAttribute(name="loginMember", required = false) Member loginMember,
				@RequestParam(name = "id") Long id, Model model) {
			//로그인 안했다면
//			if(loginMember == null) {
//				return "redirect:/member/login";
//			}
			Restaurant restaurant = restaurantService.findRestaurant(id);
			
			// 없는 board_id인 경우
			if(restaurant == null) {
				return "redirect:/shop/index";
			}
			
			model.addAttribute("restaurant", restaurant);
			return "restaurant/rtread";
		}
		
		@PostMapping("rtdelete")
		public String remove(// @SessionAttribute(name = "loginMember", required = false) Member loginMember,
							 @RequestParam(name="id", required = false) Long id) {
			
			
			restaurantService.deleteRestaurant(id);
			
			return "redirect:/";
		}
		
}
