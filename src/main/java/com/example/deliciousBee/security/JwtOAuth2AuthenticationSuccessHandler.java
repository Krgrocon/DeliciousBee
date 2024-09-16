package com.example.deliciousBee.security;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.CustomOAuth2User;
import com.example.deliciousBee.service.member.BeeMemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final BeeMemberService beeMemberService;

    public JwtOAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider, BeeMemberService beeMemberService) {
        this.tokenProvider = tokenProvider;
        this.beeMemberService = beeMemberService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email"); // 이메일을 속성에서 가져옴

            // BeeMember 찾기 또는 생성
            BeeMember beeMember = beeMemberService.findMemberByEmail(email);

            // JWT 토큰 생성
            String token = tokenProvider.generateToken(beeMember);

            // 쿠키에 JWT 설정
            Cookie jwtCookie = new Cookie("Authorization", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true); // HTTPS에서만 사용
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(86400); // 1일 동안 유효
            response.addCookie(jwtCookie);

            // 리다이렉트
            response.sendRedirect("/");
        } else {
            throw new IllegalStateException("Unexpected principal type: " + authentication.getPrincipal().getClass().getName());
        }
    }




}