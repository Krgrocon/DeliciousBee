package com.example.deliciousBee.service.member;

import com.example.deliciousBee.model.member.SocialLogin;
import com.example.deliciousBee.repository.SocialLoginRepository;
import com.example.deliciousBee.security.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SocialLoginService {

    private final SocialLoginRepository socialLoginRepository;

    // SocialLogin 저장 또는 업데이트
    public SocialLogin saveOrUpdate(OAuthAttributes attributes) {
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
