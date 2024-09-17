package com.example.deliciousBee.controller.member;

import com.example.deliciousBee.security.jwt.JwtTokenProvider;
import com.example.deliciousBee.service.member.BeeMemberService;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final BeeMemberService beeMemberService;

    // 인증 이메일 전송
    @PostMapping("/mailSend")
    @ResponseBody
    public ResponseEntity<?> mailSend(@RequestParam String email) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            // 이메일 중복 확인
            if (beeMemberService.existsByEmail(email)) {
                map.put("success", false);
                map.put("error", "이미 등록된 이메일입니다.");
                return ResponseEntity.badRequest().body(map);
            }

            int verificationCode = mailService.generateVerificationCode();
            mailService.sendMail(email, verificationCode);

            // JWT 토큰 생성
            String token = jwtTokenProvider.generateEmailVerificationToken(email, verificationCode);

            map.put("success", true);
            map.put("token", token);
            return ResponseEntity.ok(map);
        } catch (Exception e) {
            map.put("success", false);
            map.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(map);
        }
    }

    // 인증번호 일치 여부 확인
    @PostMapping("/mailCheck")
    @ResponseBody
    public ResponseEntity<?> mailCheck(@RequestParam String verificationCode, @RequestParam String token) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            if (!jwtTokenProvider.validateToken(token)) {
                map.put("success", false);
                map.put("error", "유효하지 않거나 만료된 토큰입니다.");
                return ResponseEntity.badRequest().body(map);
            }

            Integer tokenVerificationCode = jwtTokenProvider.getVerificationCodeFromJWT(token);
            String email = jwtTokenProvider.getEmailFromJWT(token);

            if (tokenVerificationCode != null && tokenVerificationCode.toString().equals(verificationCode)) {
                map.put("success", true);
                map.put("email", email);
                return ResponseEntity.ok(map);
            } else {
                map.put("success", false);
                map.put("error", "인증 번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(map);
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(map);
        }
    }

}
