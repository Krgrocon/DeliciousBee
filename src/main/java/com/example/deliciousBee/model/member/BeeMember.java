package com.example.deliciousBee.model.member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.example.deliciousBee.model.review.Review;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity // JPA가 관리
@Getter @Setter @ToString
@Builder
@AllArgsConstructor
public class BeeMember implements UserDetails {

	@Id
	@Column(length = 60)
	private String member_id;

	@Column(length = 60, nullable = false)
	private String password;


	private NationalType national;

	@Column(length = 50, nullable = false)
	private String name;

	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private GenderType gender;

	@Column(name = "birth")
	private LocalDate birth;

	@Column(length = 100)
	private String email;

	private boolean isSocialUser;  // 소셜 로그인 플래그

	@OneToMany(mappedBy = "beeMember", fetch = FetchType.EAGER)
	private List<Review> review;

	@OneToOne(mappedBy = "beeMember", cascade = CascadeType.ALL)
	@JsonIgnore
	private SocialLogin socialLogin;

	@Enumerated(EnumType.STRING)
	@NotNull
	private Role role;



	public BeeMember() {
	}

	// 연관관계 메서드
	public void setSocialLogin(SocialLogin socialLogin) {
		this.socialLogin = socialLogin;
		socialLogin.setBeeMember(this);
	}

	public String getRoleKey() {
		return this.role.getKey();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
