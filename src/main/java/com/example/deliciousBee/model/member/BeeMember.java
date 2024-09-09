package com.example.deliciousBee.model.member;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.example.deliciousBee.model.file.MemberAttachedFile;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.model.review.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;



@Entity // JPA가 관리
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeeMember implements UserDetails, OAuth2User {
	@Id
	@Column(length = 60)
	private String member_id;
	@Column(length = 60, nullable = false)
	private String password;
	@Enumerated(EnumType.STRING)
	private NationalType national;
	@Column(length = 50, nullable = false)
	private String nickname;
	@Column(length = 10)
	@Enumerated(EnumType.STRING)
	private GenderType gender;
	@Column(name = "birth")
	private LocalDate birth;
	@Column(length = 100)
	private String email;
	private boolean isSocialUser; // 소셜 로그인 플래그
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "profile_image_id") // 외래 키 컬럼 이름 지정
	private MemberAttachedFile profileImage;
	@OneToOne(mappedBy = "beeMember", cascade = CascadeType.ALL)
	@JsonIgnore
	private MyPage myPage;
	//*****************팔로워**********************
	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Follow> followingList; // 내가 팔로우하는 사람들 목록
	@OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Follow> followerList; // 나를 팔로우하는 사람들 목록
	// ************리뷰*************
	@OneToMany(mappedBy = "beeMember", fetch = FetchType.EAGER)
	private List<Review> review;
	@OneToOne(mappedBy = "beeMember", cascade = CascadeType.ALL)
	@JsonIgnore
	private SocialLogin socialLogin;
	@Enumerated(EnumType.STRING)
	@NotNull
	private Role role;
	@Transient
	private Map<String, Object> attributes; // OAuth2User의 속성을 저장할 필드
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
		return nickname;
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
	// OAuth2User 인터페이스 구현
	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	@Override
	public String getName() {
		return this.email; // OAuth2User의 getName() 메서드를 통해 반환할 사용자 식별자
	}
	// attributes를 설정하는 메서드
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	@Override
	public String toString() {
		return "BeeMember{" + "member_id='" + member_id + '\'' + ", password='" + password + '\'' + ", national="
				+ national + ", nickname='" + nickname + '\'' + ", gender=" + gender + ", birth=" + birth + ", email='"
				+ email + '\'' + ", isSocialUser=" + isSocialUser + ", role=" + role + '}';
	}
}