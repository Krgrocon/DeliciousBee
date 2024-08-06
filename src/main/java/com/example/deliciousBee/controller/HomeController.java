package com.example.deliciousBee.controller;

import java.util.List;

import com.example.deliciousBee.model.review.Restaurant;
import com.example.deliciousBee.service.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.deliciousBee.model.review.Review;
import com.example.deliciousBee.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final ReviewService reviewService;
	private final RestaurantService restaurantService;

	@GetMapping("/")
	public String restaurantlist(Model model) {
		List<Restaurant> restaurantlist = restaurantService.findAll();
		model.addAttribute("restaurantlist", restaurantlist);
		return "shop/index";
	}
}
