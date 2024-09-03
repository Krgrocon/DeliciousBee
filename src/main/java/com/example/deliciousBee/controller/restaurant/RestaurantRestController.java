
package com.example.deliciousBee.controller.restaurant;

import com.example.deliciousBee.dto.restaurant.RestaurantDto;
import com.example.deliciousBee.model.board.CategoryType;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.restaurant.RestaurantService;
import com.example.deliciousBee.util.FileService;
import com.example.deliciousBee.util.RestaurantFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final RestaurantService restaurantService;
    private final PagedResourcesAssembler<RestaurantDto> assembler;
    private final BeeMemberService beeMemberService;
    private final FileService fileService;
    private final RestaurantFileService restaurantFileService;


    @GetMapping("search")
    public ResponseEntity<PagedModel<EntityModel<RestaurantDto>>> getRestaurants(
            @RequestParam(value = "keyword", required = false) String keyword,
            Pageable pageable,
            @RequestParam(value = "sortBy", required = false, defaultValue = "default") String sortBy,
            @RequestParam(value = "latitude", required = false) Double userLatitude,
            @RequestParam(value = "longitude", required = false) Double userLongitude) {

        Page<RestaurantDto> restaurants;

        if (keyword == null || keyword.isEmpty()) {
            restaurants = restaurantService.searchRestaurants(null, pageable, sortBy, userLatitude, userLongitude);
        } else {
            restaurants = restaurantService.searchRestaurants(keyword, pageable, sortBy, userLatitude, userLongitude);
        }

        return ResponseEntity.ok(assembler.toModel(restaurants));
    }




    @PostMapping("create")
    public ResponseEntity<Restaurant> createRestaurant(@AuthenticationPrincipal BeeMember loginMember,
                                                       @RequestParam("name") String name,
                                                       @RequestParam("address") String address,
                                                       @RequestParam("phone_number") String phoneNumber,
                                                       @RequestParam("description") String description,
                                                       @RequestParam("category") String category,
                                                       @RequestParam("latitude") Double latitude,
                                                       @RequestParam("longitude") Double longitude,
                                                       @RequestParam("menu_name[]") List<String> menuNames,
                                                       @RequestParam("price_range[]") List<String> priceRanges,
                                                       @RequestPart(name = "file", required = false) MultipartFile[] files) {

        // 로그인 여부 확인
        if (loginMember == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        BeeMember findMember = beeMemberService.findMemberById(loginMember.getMember_id());

        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone_number(phoneNumber);
        restaurant.setDescription(description);
        restaurant.setCategory(CategoryType.valueOf(category));
        restaurant.setLatitude(latitude);
        restaurant.setLongitude(longitude);
        restaurant.setMember(findMember);


        List<RestaurantAttachedFile> attachedFiles = new ArrayList<>();
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // saveRestaurantFile() 메서드 사용
                    RestaurantAttachedFile attachedFile = restaurantFileService.saveFile(file, restaurant);
                    if (attachedFile != null) {
                        attachedFiles.add(attachedFile);
                    }
                }
            }
        }

        restaurantService.saveRestaurant(restaurant, attachedFiles);
        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }
}