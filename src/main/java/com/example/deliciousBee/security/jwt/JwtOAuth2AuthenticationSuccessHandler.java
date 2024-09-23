package com.example.deliciousBee.security.jwt;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.dto.member.OAuthAttributes;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.member.SocialLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final BeeMemberService beeMemberService;
    private final SocialLoginService socialLoginService;  // SocialLoginService 추가

    public JwtOAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider,
                                                 BeeMemberService beeMemberService,
                                                 SocialLoginService socialLoginService) {
        this.tokenProvider = tokenProvider;
        this.beeMemberService = beeMemberService;
        this.socialLoginService = socialLoginService;  // SocialLoginService 초기화
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof DefaultOAuth2User) {

            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email"); // 이메일 속성에서 가져옴
            String name = oAuth2User.getAttribute("name");   // 이름 속성에서 가져옴
            String picture = oAuth2User.getAttribute("picture"); // 프로필 사진 속성에서 가져옴
            String provider = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(); // 로그인 제공자 정보 (Google, Line 등)

            // OAuthAttributes 생성
            OAuthAttributes oAuthAttributes = OAuthAttributes.builder()
                    .email(email)
                    .name(name)
                    .picture(picture)
                    .provider(provider)
                    .build();

            // SocialLogin 저장 또는 업데이트
            socialLoginService.saveOrUpdate(oAuthAttributes);

            // BeeMember 찾기 또는 생성
            BeeMember beeMember = beeMemberService.findOrCreateBeeMember(oAuthAttributes);

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
