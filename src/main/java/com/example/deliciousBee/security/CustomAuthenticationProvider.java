//package com.example.deliciousBee.security;
//
//import com.example.deliciousBee.model.member.BeeMember;
//import com.example.deliciousBee.model.member.CustomOAuth2User;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.stereotype.Component;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.authentication.AuthenticationProvider;
//
//@Component
//public class CustomAuthenticationProvider implements AuthenticationProvider {
//
//    @Override
//    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        if (authentication instanceof OAuth2AuthenticationToken) {
//            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
//
//            BeeMember beeMember = ((CustomOAuth2User) oauthToken.getPrincipal()).getBeeMember();
//
//            return new OAuth2AuthenticationToken(
//                    new CustomOAuth2User(oauthToken.getAuthorities(), oauthToken.getPrincipal().getAttributes(), beeMember),
//                    oauthToken.getAuthorities(),
//                    oauthToken.getAuthorizedClientRegistrationId()
//            );
//        }
//
//        return null;
//    }
//
//    @Overridede
//    public boolean supports(Class<?> authentication) {
//        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
//    }
//}
