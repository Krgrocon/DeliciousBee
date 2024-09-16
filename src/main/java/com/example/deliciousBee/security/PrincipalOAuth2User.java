package com.example.deliciousBee.security;

import com.example.deliciousBee.model.member.BeeMember;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class PrincipalOAuth2User implements OAuth2User {
    private final BeeMember beeMember;
    private final OAuth2User oAuth2User;

    public PrincipalOAuth2User(BeeMember beeMember, OAuth2User oAuth2User) {
        this.beeMember = beeMember;
        this.oAuth2User = oAuth2User;
    }

    public BeeMember getBeeMember() {
        return beeMember;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }
}
