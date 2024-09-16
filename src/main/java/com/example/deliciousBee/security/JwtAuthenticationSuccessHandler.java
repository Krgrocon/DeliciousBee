package com.example.deliciousBee.security;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String token = tokenProvider.generateToken(authentication);

        // Set JWT as an HTTP-only cookie
        Cookie jwtCookie = new Cookie("Authorization", "Bearer " + token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false); // Set to true in production (requires HTTPS)
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(86400); // 1 day in seconds

        response.addCookie(jwtCookie);

        // Optionally, you can still return the token in the response body if needed
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"Login successful\"}");
    }
}
