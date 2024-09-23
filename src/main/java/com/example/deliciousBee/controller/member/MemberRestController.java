package com.example.deliciousBee.controller.member;


import com.example.deliciousBee.model.member.BeeLoginForm;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.Role;
import com.example.deliciousBee.security.jwt.JwtTokenProvider;
import com.example.deliciousBee.service.member.BeeMemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller // 응답 html
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberRestController {


    private final BeeMemberService beeMemberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

//
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@RequestBody BeeLoginForm loginRequest) {
//        // 사용자가 입력한 ID로 회원 찾기
//        BeeMember beeMember = beeMemberService.findMemberById(loginRequest.getMember_id());
//
//        // 사용자가 존재하지 않거나 비밀번호가 틀린 경우 처리
//        if (beeMember == null || !passwordEncoder.matches(loginRequest.getPassword(), beeMember.getPassword())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID or password");
//        }
//
//        // JWT 토큰 생성
//        String token = jwtTokenProvider.generateToken(beeMember);
//
//        // 클라이언트에게 JWT 토큰 반환
//        return ResponseEntity.ok(new JwtResponse(token));
//    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody BeeLoginForm loginRequest, HttpServletResponse response) {
        // 사용자가 입력한 ID로 회원 찾기
        BeeMember beeMember = beeMemberService.findMemberById(loginRequest.getMember_id());

        // 사용자가 존재하지 않거나 비밀번호가 틀린 경우 처리
        if (beeMember == null || !passwordEncoder.matches(loginRequest.getPassword(), beeMember.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid ID or password");
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(beeMember);

        // 쿠키에 JWT를 설정
        Cookie jwtCookie = new Cookie("Authorization", token);
        jwtCookie.setHttpOnly(true);  // 클라이언트에서 접근 불가
        jwtCookie.setSecure(true);    // HTTPS에서만 사용
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400);   // 1일 동안 유효
        response.addCookie(jwtCookie);

        // 성공 응답
        return ResponseEntity.ok("로그인 성공");
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("Authorization", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);  // 쿠키 삭제
        response.addCookie(jwtCookie);
        return ResponseEntity.ok("로그아웃 성공");
    }



    @GetMapping("/api/check-auth")
    public ResponseEntity<?> checkAuth(@CookieValue(name = "Authorization", required = false) String token) {
        if (token == null) {
            System.out.println("Authorization 쿠키가 없음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인되지 않았습니다.");
        }
        System.out.println("Authorization 쿠키 값: " + token);

        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
        }

        // 토큰에서 사용자 ID 추출
        String memberId = jwtTokenProvider.getMemberIdFromJWT(token);
        BeeMember beeMember = beeMemberService.findMemberById(memberId); // DB 또는 서비스에서 사용자 정보 조회

        if (beeMember == null || beeMember.getRole() == null) {
            // 사용자 또는 역할이 없으면 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보가 유효하지 않습니다.");
        }

        // 필요한 사용자 정보를 응답으로 보냄 (예: 로그인 상태, 관리자 여부)
        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", true);
        response.put("username", beeMember.getUsername());

        // Enum을 직접 비교하여 관리자인지 확인
        response.put("isAdmin", beeMember.getRole() == Role.ADMIN);

        return ResponseEntity.ok(response);
    }


    // 사용자 정보를 조회하는 서비스 (DB 연동)
    private BeeMember findMemberById(String memberId) {
        // 이 부분에서 사용자 정보를 데이터베이스에서 조회하거나 필요한 로직을 추가
        // 예: UserRepository.findById(memberId)
        return new BeeMember();  // 예시로 BeeMember 객체를 반환
    }

}
