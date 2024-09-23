package com.example.deliciousBee.dto.member;

import java.util.Map;

import com.example.deliciousBee.model.member.SocialLogin;
import lombok.Builder;
import lombok.Getter;



@Getter
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    private final String picture;
    private final String provider;  // provider 필드를 final로 설정

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, String provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;  // provider 필드 초기화
    }

    // of() 메서드에서 OAuthAttributes를 생성할 때 provider 값도 추가
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("line".equals(registrationId)) {
            return ofLine(userNameAttributeName, attributes);
        }
        // 다른 OAuth2 제공자(Google 등)의 경우 추가
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofLine(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider("line")  // LINE에 대한 provider 값 설정
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .provider("google")  // Google에 대한 provider 값 설정
                .build();
    }

    // SocialLogin 엔티티 생성 메서드
    public SocialLogin toEntity() {
        return SocialLogin.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .provider(provider)  // provider 설정
                .build();
    }
    // 명시적으로 provider getter 메서드 추가 (필요할 경우)
    public String getProvider() {
        return provider;
    }
}
