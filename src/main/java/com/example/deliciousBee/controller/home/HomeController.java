package com.example.deliciousBee.controller.home;

import java.util.List;

import com.example.deliciousBee.service.restaurant.RestaurantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.deliciousBee.model.board.Restaurant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final RestaurantService restaurantService;

	@GetMapping("/")
	public String restaurantlist(Model model) {
		List<Restaurant> restaurantlist = restaurantService.findRandom5Restaurants();
		model.addAttribute("restaurantlist", restaurantlist);
		return "shop/index";
	}
}
