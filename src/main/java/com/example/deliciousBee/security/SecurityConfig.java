package com.example.deliciousBee.security;

import com.example.deliciousBee.model.member.Role;
import com.example.deliciousBee.service.member.BeeMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final BeeMemberService beeMemberService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, BeeMemberService beeMemberService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.beeMemberService = beeMemberService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf((csrfConfig) -> csrfConfig.disable())
                .headers((headerConfig) -> headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/restaurant/rtwrite", "/comments/save").hasRole("USER")
                        .requestMatchers("/", "/member/login", "/css/**", "images/**", "/js/**", "/login/*", "/logout/*", "/posts/**", "/comments/**", "/follow/**", "/unfollow/**" , "/restaurant/display/**" , "/image/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exceptions) -> exceptions
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/member/login");
                        })
                )
                .formLogin((formLogin) -> formLogin
                        .loginPage("/member/login")
                        .usernameParameter("member_id")
                        .loginProcessingUrl("/member/login")
                        .successHandler(new CustomAuthenticationSuccessHandler())
                        .permitAll()
                )
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .logout((logoutConfig) -> logoutConfig.logoutSuccessUrl("/"))
                .oauth2Login((oauth) -> oauth
                        .loginPage("/member/login")
                        .userInfoEndpoint((endpoint) -> endpoint.userService(customOAuth2UserService))
                        .successHandler(new CustomAuthenticationSuccessHandler())
                );

        return http.build();
    }


    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(beeMemberService);
        return provider;
    }
    
}

