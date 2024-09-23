package com.example.deliciousBee.service.member;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.example.deliciousBee.dto.member.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.member.BeeMember;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final BeeMemberService beeMemberService;
    private final SocialLoginService socialLoginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());

        // LINE과 Google 모두에 대해 'sub' 속성 처리
        if ("line".equals(registrationId)) {
            if (attributes.containsKey("userId")) {
                attributes.put("sub", attributes.get("userId"));
            } else {
                throw new OAuth2AuthenticationException("Missing 'userId' in LINE user info");
            }
        } else if ("google".equals(registrationId)) {
            if (!attributes.containsKey("sub")) {
                throw new OAuth2AuthenticationException("Missing 'sub' in Google user info");
            }
        }

        // 'sub'를 항상 userNameAttributeName으로 사용
        userNameAttributeName = "sub";

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        // SocialLogin 저장 또는 업데이트는 SocialLoginService로 위임
        socialLoginService.saveOrUpdate(oAuthAttributes);

        // BeeMember 찾기 또는 생성은 BeeMemberService로 위임
        BeeMember beeMember = beeMemberService.findOrCreateBeeMember(oAuthAttributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(beeMember.getRoleKey())),
                attributes,
                userNameAttributeName
        );
    }


}
