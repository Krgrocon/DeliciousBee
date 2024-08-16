package com.example.deliciousBee.RestaurantData;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class InputData {


    @Test
    public void TestData() {
        String result = ""; // 결과를 저장할 변수 초기화

        try { // 예외 처리 블록 시작
            // 요청 날짜를 변수에 저장
            URL url = new URL("https://api.odcloud.kr/api/15096697/v1/uddi:0bb27225-00f4-4a12-907e-e5afe49f90db?page=0&perPage=100&returnType=json&serviceKey=pIHAHs98LUFwkTdb1JFNu5O%2B3f%2FTiJ7ItvFW89FWJvwB%2FNo7RyHYGSZ3jlHfwhjN%2FrTzYxuQmMVRoIV1Vs0sow%3D%3D"); // API URL 생성
            BufferedReader bf; // BufferedReader 객체 선언
            bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")); // API에서 데이터를 읽어올 BufferedReader 객체 생성 (UTF-8 인코딩)
            result = bf.readLine(); // API 응답에서 첫 번째 줄을 읽어서 result 변수에 저장
            System.out.println(result);
            JSONParser jsonParser = new JSONParser(); // JSON 파싱 객체 생성
            JSONObject jsonObject = (JSONObject) jsonParser.parse(result); // result를 JSON 객체로 파싱
            JSONObject RestaurantData = (JSONObject) jsonObject.get("results"); // JSON 객체에서 "CardSubwayStatsNew" 키에 해당하는 객체 추출
            System.out.println("첫번째 관문");
//            Long totalCount = (Long) RestaurantData.get("currentCount"); // JSON 객체에서 "list_total_count" 키에 해당하는 값을 Long 타입으로 추출하여 totalCount 변수에 저장
            System.out.println("두번째 관문");

            JSONArray infoArr = (JSONArray) jsonObject.get("data"); // JSON 객체에서 "row" 키에 해당하는 배열 추출

            for (int i = 0; i < infoArr.size(); i++) {
                JSONObject info = (JSONObject) infoArr.get(i);
                System.out.println(info);
                String name = (String)info.get("식당명");
                System.out.println(name);

            }
//            for (int i = 0; i < infoArr.size(); i++) {
//                JSONObject info = (JSONObject) infoArr.get(i);
//
//                String name = (String) info.get("식당명");
//                String description = (String) info.get("음식점소개내용");
//                double longitude = Double.parseDouble((String) tmp.get("식당경도"));
//                double latitude = Double.parseDouble((String) tmp.get("식당위도"));
//
//                Restaurant restaurant = new Restaurant(name, description, longitude, latitude);
//
//            }

        } catch (Exception e) { // 예외 처리 블록
            e.printStackTrace(); // 예외 발생 시 콘솔에 예외 정보 출력
        }
    }


    @Test
    public void TestData1() {
        String token = "hRaN10GhwJnTXc56l1mJ8giloduais4UhvSMOt71n7E6u37OReMhTXNb68cXkURD";
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);
        System.out.println(encodedToken);
    }

    @Test
    public void TestData2() {
            String result = ""; // 결과를 저장할 변수 초기화

            try { // 예외 처리 블록 시작
                URL url = new URL("https://api.odcloud.kr/api/15096697/v1/uddi:0bb27225-00f4-4a12-907e-e5afe49f90db?page=0&perPage=100&returnType=json&serviceKey=pIHAHs98LUFwkTdb1JFNu5O%2B3f%2FTiJ7ItvFW89FWJvwB%2FNo7RyHYGSZ3jlHfwhjN%2FrTzYxuQmMVRoIV1Vs0sow%3D%3D"); // API URL 생성
                BufferedReader bf; // BufferedReader 객체 선언
                bf = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")); // API에서 데이터를 읽어올 BufferedReader 객체 생성 (UTF-8 인코딩)
                result = bf.readLine(); // API 응답에서 첫 번째 줄을 읽어서 result 변수에 저장
                System.out.println(result);
                JSONParser jsonParser = new JSONParser(); // JSON 파싱 객체 생성
                JSONObject jsonObject = (JSONObject) jsonParser.parse(result);
                JSONObject RestaurantData = (JSONObject) jsonObject.get("results");
                System.out.println("첫번째 관문");
                System.out.println("두번째 관문");
                JSONArray infoArr = (JSONArray) jsonObject.get("data"); // JSON

                for (int i = 0; i < infoArr.size(); i++) {
                    JSONObject info = (JSONObject) infoArr.get(i);
                    System.out.println(info);
                    String name = (String)info.get("식당명");
                    System.out.println(name);

                }
//            for (int i = 0; i < infoArr.size(); i++) {
//                JSONObject info = (JSONObject) infoArr.get(i);
//
//                String name = (String) info.get("식당명");
//                String description = (String) info.get("음식점소개내용");
//                double longitude = Double.parseDouble((String) tmp.get("식당경도"));
//                double latitude = Double.parseDouble((String) tmp.get("식당위도"));
//
//                Restaurant restaurant = new Restaurant(name, description, longitude, latitude);
//
//            }

            } catch (Exception e) { // 예외 처리 블록
                e.printStackTrace(); // 예외 발생 시 콘솔에 예외 정보 출력
            }
        }
    }
