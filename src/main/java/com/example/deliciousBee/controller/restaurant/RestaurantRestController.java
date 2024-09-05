
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

        // BeeMember를 데이터베이스에서 찾음
        BeeMember findMember = beeMemberService.findMemberById(loginMember.getMember_id());
        System.out.println("Credentials path: " + this.getClass().getClassLoader().getResource("deliciousbee-acb114448e3c.json"));

        // 레스토랑 객체 생성 및 속성 설정
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone_number(phoneNumber);
        restaurant.setDescription(description);
//        Set<CategoryType> categories = new HashSet<>();
//        for (CategoryType categoryType : restaurantRequest.getCategories()) {
//            categories.add(categoryType);
//        }
//        restaurant.setCategories(categories);

        restaurant.setLatitude(latitude);
        restaurant.setLongitude(longitude);
        restaurant.setMember(findMember);

        // 업로드한 파일 정보를 저장할 리스트
        List<RestaurantAttachedFile> attachedFiles = new ArrayList<>();

        // 파일 업로드 처리 (Google Cloud Storage)
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // GCS에 파일 저장
                        AttachedFile uploadedFile = fileService.saveFile(file);

                        // 업로드된 파일 정보를 RestaurantAttachedFile에 매핑
                        if (uploadedFile != null) {
                            RestaurantAttachedFile attachedFile = new RestaurantAttachedFile();
                            attachedFile.setRestaurant(restaurant);
                            attachedFile.setOriginal_filename(uploadedFile.getOriginal_filename());
                            attachedFile.setSaved_filename(uploadedFile.getSaved_filename());
                            attachedFile.setFile_size(uploadedFile.getFile_size());
                            attachedFiles.add(attachedFile);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 파일 저장 실패 시
                    }
                }
            }
        }

        // 레스토랑과 첨부 파일 정보를 데이터베이스에 저장
        restaurantService.saveRestaurant(restaurant, attachedFiles);

        return new ResponseEntity<>(restaurant, HttpStatus.CREATED);
    }




}