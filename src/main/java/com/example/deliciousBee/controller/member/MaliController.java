package com.example.deliciousBee.controller.member;

import com.example.deliciousBee.service.member.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("member")
public class MaliController {

    private final MailService mailService;
    private int number; // 이메일 인증 숫자를 저장하는 변수

//    // 인증 이메일 전송
//    @PostMapping("/mailSend")
//    public HashMap<String, Object> mailSend(String mail) {
//        HashMap<String, Object> map = new HashMap<>();
//        System.out.println("mailSend");
//        try {
//            number = mailService.sendMail(mail);
//            String num = String.valueOf(number);
//
//            System.out.println("mailSend");
//            map.put("success", Boolean.TRUE);
//            map.put("number", num);
//            System.out.println("mailSend");
//        } catch (Exception e) {
//            map.put("success", Boolean.FALSE);
//            map.put("error", e.getMessage());
//        }
//
//        return map;
//    }

    @PostMapping("/mailSend")
    public ResponseEntity<?> mailSend(String email) {
        HashMap<String, Object> map = new HashMap<>();
        System.out.println("mailSend");
        try {
            number = mailService.sendMail(email);
            String num = String.valueOf(number);

            System.out.println("mailSend");
            map.put("success", Boolean.TRUE);
            map.put("number", num);
            System.out.println("mailSend");
            return ResponseEntity.ok(map); // ResponseEntity와 함께 map 반환
        } catch (Exception e) {
            map.put("success", Boolean.FALSE);
            map.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(map); // 오류 응답 반환
        }
    }

    // 인증번호 일치여부 확인
    @GetMapping("/mailCheck")
    public ResponseEntity<?> mailCheck(@RequestParam String userNumber) {

        boolean isMatch = userNumber.equals(String.valueOf(number));

        return ResponseEntity.ok(isMatch);
    }
}
