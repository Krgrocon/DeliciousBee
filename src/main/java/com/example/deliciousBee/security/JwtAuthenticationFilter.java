package com.example.deliciousBee.security;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.service.member.BeeMemberService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final BeeMemberService beeMemberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt)) {
            System.out.println("JWT Token from request: " + jwt);
            if (tokenProvider.validateToken(jwt)) {
                String memberId = tokenProvider.getMemberIdFromJWT(jwt);
                System.out.println("Extracted memberId from JWT: " + memberId);

                // loadUserByUsername이 정상적으로 동작하는지 확인
                BeeMember beeMember = (BeeMember) beeMemberService.loadUserByUsername(memberId);
                if (beeMember == null) {
                    System.out.println("BeeMember not found for memberId: " + memberId);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid user");
                    return;
                }

                // UsernamePasswordAuthenticationToken 생성 및 SecurityContext에 설정
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        beeMember, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_" + beeMember.getRole().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("Authentication set for memberId: " + memberId);
            } else {
                System.out.println("Invalid JWT token");
                // JWT가 유효하지 않더라도 필터 체인을 계속 실행
            }
        } else {
            System.out.println("No JWT token found in request");
            // JWT가 없더라도 필터 체인을 계속 실행
        }

        // 다음 필터로 요청을 넘깁니다.
        filterChain.doFilter(request, response);
    }


    // Extract JWT token from Authorization header
    private String getJwtFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();  // 쿠키에서 JWT 반환
                }
            }
        }
        return null;
    }

}
