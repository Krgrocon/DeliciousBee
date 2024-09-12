
package com.example.deliciousBee.controller.restaurant;

import com.example.deliciousBee.dto.restaurant.RestaurantDto;
import com.example.deliciousBee.model.board.CategoryType;
import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.file.AttachedFile;
import com.example.deliciousBee.model.file.RestaurantAttachedFile;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.menu.Menu;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
                                                       @RequestParam("true-address") String address,
                                                       @RequestParam("phone_number") String phoneNumber,
                                                       @RequestParam("description") String description,
                                                       @RequestParam("categories") String categories,
                                                       @RequestParam("mainCategory") String mainCategory,
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
        
        restaurant.setCategories(categories);
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone_number(phoneNumber);
        restaurant.setDescription(description);
        restaurant.setMainCategory(mainCategory);
        restaurant.setLatitude(latitude);
        restaurant.setLongitude(longitude);
        restaurant.setMember(findMember);
        
        // 메뉴 추가 로직
        List<Menu> menus = new ArrayList<>();
        for (int i = 0; i < menuNames.size(); i++) {
            Menu menu = new Menu();
            menu.setName(menuNames.get(i));
            menu.setPrice(priceRanges.get(i));
            menu.setRestaurant(restaurant);
            menus.add(menu);
        }
        restaurant.setMenuList(menus);
        
        

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
    
//    @PutMapping("/update/{id}")
//    public ResponseEntity<?> update(@PathVariable("id") Long id, 
//                                     @AuthenticationPrincipal BeeMember loginMember,
//                                     @RequestParam("name") String name,
//                                     @RequestParam("address") String address,
//                                     @RequestParam("phone_number") String phoneNumber,
//                                     @RequestParam("description") String description,
//                                     @RequestParam("category") CategoryType category,
//                                     @RequestParam("mainCategory") String mainCategory,
//                                     @RequestParam("latitude") Double latitude,
//                                     @RequestParam("longitude") Double longitude,
//                                     @RequestPart(name = "files", required = false) MultipartFile[] files,
//                                     BindingResult result) {
//
//      // 로그인 여부 확인 및 권한 검증 (필요에 따라 추가)
//      if (loginMember == null) {
//          return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//      }
//
//      BeeMember findMember = beeMemberService.findMemberById(loginMember.getMember_id());
//      
//      // 입력값 검증
//      if (result.hasErrors()) {
//        return ResponseEntity.badRequest().body(result.getAllErrors());
//      }
//
//      // 레스토랑 존재 여부 확인
//      if (!restaurantService.existsById(id)) {
//        return ResponseEntity.notFound().build();
//      }
//
//      Restaurant existingRestaurant = restaurantService.findRestaurant(id);
//      
//      existingRestaurant.setName(name);
//      existingRestaurant.setAddress(address);
//      existingRestaurant.setPhone_number(phoneNumber);
//      existingRestaurant.setDescription(description);
//      existingRestaurant.setCategory(category);
//      existingRestaurant.setMainCategory(mainCategory);
//      existingRestaurant.setLatitude(latitude);
//      existingRestaurant.setLongitude(longitude);
//      existingRestaurant.setMember(findMember);
//      
//      List<RestaurantAttachedFile> attachedFiles = new ArrayList<>();
//
//      // 파일 업로드 처리 (Google Cloud Storage)
//      if (files != null && files.length > 0) {
//          for (MultipartFile file : files) {
//              if (!file.isEmpty()) {
//                  try {
//                      // GCS에 파일 저장
//                      AttachedFile uploadedFile = fileService.saveFile(file);
//
//                      // 업로드된 파일 정보를 RestaurantAttachedFile에 매핑
//                      if (uploadedFile != null) {
//                          RestaurantAttachedFile attachedFile = new RestaurantAttachedFile();
//                          attachedFile.setRestaurant(existingRestaurant);
//                          attachedFile.setOriginal_filename(uploadedFile.getOriginal_filename());
//                          attachedFile.setSaved_filename(uploadedFile.getSaved_filename());
//                          attachedFile.setFile_size(uploadedFile.getFile_size());
//                          attachedFiles.add(attachedFile);
//                      }
//                  } catch (IOException e) {
//                      e.printStackTrace();
//                      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 파일 저장 실패 시
//                  }
//              }
//          }
//      }
//      
//      // 레스토랑 정보 업데이트
//      restaurantService.updateRestaurant(existingRestaurant, attachedFiles); 
//
//      return ResponseEntity.ok(existingRestaurant);
//    }

}