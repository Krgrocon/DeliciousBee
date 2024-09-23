package com.example.deliciousBee.model.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class SocialLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    private BeeMember beeMember;

    @NotNull
    private String email;

    @NotNull
    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String picture;


    @NotNull
    private String provider;  // OAuth 제공자 필드

    // 기본 생성자 (JPA를 위한)
    protected SocialLogin() {
    }

    // 빌더 패턴을 통해 엔티티를 쉽게 생성
    @Builder
    public SocialLogin(String name, String email, String picture, Role role, String provider, BeeMember beeMember) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.provider = provider;  // provider 필드 초기화
        this.beeMember = beeMember;
    }

    // 사용자가 이미 존재할 경우 이름과 사진을 업데이트
    public SocialLogin update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

}
