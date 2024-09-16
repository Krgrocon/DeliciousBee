package com.example.deliciousBee.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.example.deliciousBee.model.member.Role;
import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.repository.SocialLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.SocialLogin;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final BeeMemberRepository beeMemberRepository;
    private final SocialLoginRepository socialLoginRepository;

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

        SocialLogin socialLogin = saveOrUpdate(oAuthAttributes);
        BeeMember beeMember = findOrCreateBeeMember(oAuthAttributes);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(beeMember.getRoleKey())),
                attributes,
                userNameAttributeName
        );
    }

    // BeeMember 찾기 또는 생성
    private BeeMember findOrCreateBeeMember(OAuthAttributes attributes) {
        return beeMemberRepository.findByEmail(attributes.getEmail())
                .orElseGet(() -> {
                    BeeMember newBeeMember = BeeMember.builder()
                            .member_id(UUID.randomUUID().toString())
                            .password(UUID.randomUUID().toString()) // 비밀번호는 필요 없을 경우 null로 설정
                            .email(attributes.getEmail())
                            .nickname(attributes.getName())
                            .isSocialUser(true)
                            .role(Role.USER)
                            .build();

                    return beeMemberRepository.save(newBeeMember);
                });
    }

    // SocialLogin 저장 또는 업데이트
    private SocialLogin saveOrUpdate(OAuthAttributes attributes) {
        SocialLogin user = socialLoginRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture())) // 기존 사용자 업데이트
                .orElseGet(() -> {
                    SocialLogin newUser = attributes.toEntity();
                    newUser.setProvider(attributes.getProvider());  // provider 필드 설정
                    return newUser;
                });

        return socialLoginRepository.save(user);
    }
}
