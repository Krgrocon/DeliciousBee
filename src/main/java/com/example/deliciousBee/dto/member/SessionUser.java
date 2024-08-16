package com.example.deliciousBee.dto.member;

import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.Role;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable { // 직렬화 기능을 가진 세션 DTO

    // 인증된 사용자 정보만 필요 => name, email, picture 필드만 선언
    private String member_id;
    private String name;
    private String email;
    private Role role;

    public SessionUser(BeeMember beeMember) {
        this.name = beeMember.getName();
        this.email = beeMember.getEmail();
        this.member_id = beeMember.getMember_id();
        this.role = beeMember.getRole();
    }
}