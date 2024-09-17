package com.example.deliciousBee.security;

import com.example.deliciousBee.security.jwt.JwtAuthenticationFilter;
import com.example.deliciousBee.security.jwt.JwtOAuth2AuthenticationSuccessHandler;
import com.example.deliciousBee.security.jwt.JwtTokenProvider;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.member.CustomOAuth2UserService;
import com.example.deliciousBee.service.member.SocialLoginService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.oauth2.client.oidc.authentication.OidcAuthorizationCodeAuthenticationProvider;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenValidator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final BeeMemberService beeMemberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialLoginService socialLoginService;

    @Autowired
    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, BeeMemberService beeMemberService, JwtTokenProvider jwtTokenProvider , SocialLoginService socialLoginService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.beeMemberService = beeMemberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.socialLoginService = socialLoginService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(beeMemberService);  // 사용자 정보 로드
        authProvider.setPasswordEncoder(passwordEncoder());    // 비밀번호 인코딩 설정
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider, beeMemberService);

        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // 헤더 설정
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/restaurant/rtwrite", "/comments/save").hasRole("USER")
                        .requestMatchers("/", "/member/login", "/member/mailSend", "/member/mailCheck","/member/join", "/css/**", "images/**", "/js/**", "/login/**", "/logout/**", "/posts/**", "/comments/**", "/follow/**", "/unfollow/**", "/restaurant/display/**", "/image/**", "/restaurant/search", "/api/restaurants/search", "/member/api/check-auth", "/oauth2/**").permitAll() // 인증 없이 접근 가능
                        .anyRequest().authenticated() // 그 외의 요청은 인증 필요
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 세션 설정 변경
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/member/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(new JwtOAuth2AuthenticationSuccessHandler(jwtTokenProvider, beeMemberService , socialLoginService))
                        .failureHandler((request, response, exception) -> {
                            System.out.println("OAuth2 Authentication failed: " + exception.getMessage());
                            exception.printStackTrace(); // 스택 트레이스 출력
                            response.sendRedirect("/member/login?error");
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        // 커스텀 JwtDecoderFactory를 Authentication Provider에 주입
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        if (authenticationManager instanceof ProviderManager) {
            ProviderManager providerManager = (ProviderManager) authenticationManager;
            for (AuthenticationProvider provider : providerManager.getProviders()) {
                if (provider instanceof OidcAuthorizationCodeAuthenticationProvider) {
                    OidcAuthorizationCodeAuthenticationProvider oidcProvider = (OidcAuthorizationCodeAuthenticationProvider) provider;
                    oidcProvider.setJwtDecoderFactory(idTokenDecoderFactory());
                }
            }
        }

        // JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
        OidcIdTokenDecoderFactory idTokenDecoderFactory = new OidcIdTokenDecoderFactory();
        idTokenDecoderFactory.setJwsAlgorithmResolver(clientRegistration -> {
            if ("line".equals(clientRegistration.getRegistrationId())) {
                return MacAlgorithm.HS256; // LINE의 경우 HS256 알고리즘 사용
            } else {
                return SignatureAlgorithm.RS256; // 기본 RS256 알고리즘 사용
            }
        });
        idTokenDecoderFactory.setJwtValidatorFactory(clientRegistration -> {
            // 필요 시 추가 검증 로직 설정 가능
            OidcIdTokenValidator validator = new OidcIdTokenValidator(clientRegistration);
            return new DelegatingOAuth2TokenValidator<>(validator);
        });
        return idTokenDecoderFactory;
    }

}
