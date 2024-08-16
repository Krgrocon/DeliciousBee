package com.example.deliciousBee.security;
import java.util.Collections;
import java.util.UUID;

import com.example.deliciousBee.dto.oauth.OAuthAttributes;
import com.example.deliciousBee.dto.member.SessionUser;
import com.example.deliciousBee.model.member.Role;
import com.example.deliciousBee.repository.BeeMemberRepository;
import com.example.deliciousBee.repository.SocialLoginRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.SocialLogin;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final BeeMemberRepository beeMemberRepository;
    private final SocialLoginRepository socialLoginRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            System.out.println("CustomOAuth2UserService.loadUser");
            OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            // 로그인 진행 중인 서비스를 구분
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            System.out.println("CustomOAuth2UserService.loadUser2");
            // OAuth2 로그인 진행 시 키가 되는 필드 값
            String userNameAttributeName = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();
            System.out.println("CustomOAuth2UserService.loadUser3");
            // OAuth2UserService를 통해 가져온 OAuth2User의 attribute 등을 담을 클래스
            OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
            System.out.println("CustomOAuth2UserService.loadUser4");
            // 사용자 저장 또는 업데이트
            SocialLogin socialLogin = saveOrUpdate(attributes);
            // Beemember 저장 또는 업데이트
            BeeMember beeMember = findOrCreateBeeMember(socialLogin);
            // 세션에 사용자 정보 저장
            httpSession.setAttribute("loginMember", new SessionUser(beeMember));
            System.out.println("CustomOAuth2UserService.loadUser");

            return new DefaultOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(socialLogin.getRoleKey())),
                    attributes.getAttributes(),
                    attributes.getNameAttributeKey());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;  // 예외가 발생했을 경우 다시 던집니다.
        }
    }

    private BeeMember findOrCreateBeeMember(SocialLogin socialLogin) {
        return beeMemberRepository.findByEmail(socialLogin.getEmail())
                .orElseGet(() -> {
                    BeeMember newBeeMember = BeeMember.builder()
                            .member_id(UUID.randomUUID().toString())
                            .password(UUID.randomUUID().toString())
                            .email(socialLogin.getEmail())
                            .name(socialLogin.getName())
                            .isSocialUser(true)
                            .role(Role.USER)
                            .build();

                    // BeeMember와 SocialLogin 간의 연관관계 설정
                    newBeeMember.setSocialLogin(socialLogin);
                    socialLogin.setBeeMember(newBeeMember);

                    return beeMemberRepository.save(newBeeMember);
                });
    }

    private SocialLogin saveOrUpdate(OAuthAttributes attributes) {
        SocialLogin user = socialLoginRepository.findByEmail(attributes.getEmail())
                // 구글 사용자 정보 업데이트(이미 가입된 사용자) => 업데이트
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                // 가입되지 않은 사용자 => User 엔티티 생성
                .orElse(attributes.toEntity());

        return socialLoginRepository.save(user);
    }
}
