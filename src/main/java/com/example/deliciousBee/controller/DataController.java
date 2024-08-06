package com.example.deliciousBee.controller;


import com.example.deliciousBee.model.review.Restaurant;
import com.example.deliciousBee.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


@Controller
@RequiredArgsConstructor
public class DataController {

    private final RestaurantService restaurantService;


    // 한번만 누르길 두번 누르면 똑같은 값 두번 들어감.
    @GetMapping("/saveapi")
    public String TestData() {
        String result = ""; // 결과를 저장할 변수 초기화

        try {
            URL url = new URL("https://api.odcloud.kr/api/15096697/v1/uddi:0bb27225-00f4-4a12-907e-e5afe49f90db?page=0&perPage=100&returnType=json&serviceKey=pIHAHs98LUFwkTdb1JFNu5O%2B3f%2FTiJ7ItvFW89FWJvwB%2FNo7RyHYGSZ3jlHfwhjN%2FrTzYxuQmMVRoIV1Vs0sow%3D%3D"); // API URL 생성
            //위 url에서 perpage를 100으로 설정해놓음 (데이터 100개만 들고오기), 저 perpage를 6000으로 설정시 6000개 가져옴 맥시멈 약 6100

            BufferedReader bf; // BufferedReader 객체 선언
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")); // API에서 데이터를 읽어올 BufferedReader 객체 생성 (UTF-8 인코딩)
            result = bf.readLine(); // API 응답에서 첫 번째 줄을 읽어서 result 변수에 저장
            System.out.println(result);
            JSONParser jsonParser = new JSONParser(); // JSON 파싱 객체 생성
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result); // result를 JSON 객체로 파싱
            JSONObject RestaurantData = (JSONObject) jsonObject.get("results"); // JSON 객체에서 results 키에 해당하는 객체 추출
            JSONArray infoArr = (JSONArray) jsonObject.get("data"); // JSON 객체에서 data 키에 해당하는 배열 추출

            for (int i = 0; i < infoArr.size(); i++) {	
                JSONObject info = (JSONObject) infoArr.get(i);

                String name = (String) info.get("식당명");
                String description = (String) info.get("음식점소개내용");
                double longitude = Double.parseDouble((String) info.get("식당경도"));
                double latitude = Double.parseDouble((String) info.get("식당위도"));

                Restaurant restaurant = new Restaurant(name, description, longitude, latitude);
                restaurantService.saveRestaurant(restaurant);
            }

        } catch (Exception e) { // 예외 처리 블록
            e.printStackTrace(); // 예외 발생 시 콘솔에 예외 정보 출력
        }
        return "shop/index";

    }
}
