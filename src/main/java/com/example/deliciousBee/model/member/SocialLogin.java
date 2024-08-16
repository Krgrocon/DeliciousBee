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

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    // 기본 생성자 추가
    protected SocialLogin() {
    }

    @Builder
    public SocialLogin(String name, String email, String picture, Role role, BeeMember beeMember) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
        this.beeMember = beeMember;
    }

    public SocialLogin update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
