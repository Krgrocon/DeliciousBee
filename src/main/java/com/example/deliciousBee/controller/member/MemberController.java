package com.example.deliciousBee.controller.member;

import java.io.IOException;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.member.*;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.repository.MyPageVisitRepository;
import com.example.deliciousBee.security.jwt.JwtTokenProvider;
import com.example.deliciousBee.service.member.BeeMemberService;
import com.example.deliciousBee.service.member.MyPageService;

import com.example.deliciousBee.util.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller // 응답 html
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {

	private final BeeMemberService beeMemberService;
	private final HttpSession session;
	private final BCryptPasswordEncoder passwordEncoder;
	private final MyPageService myPageService;
	private final MyPageVisitRepository myPageVisitRepository;
	private final JwtTokenProvider jwtTokenProvider;



	@Autowired
	private FileService fileService; // fileStore 주입 받음.

	// **************회원가입 페이지 이동*************
	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("member", new BeeJoinForm()); // new Member(): 빈객체를 담아서 보낸다-> member가가지고있는 필드이름으 활용할수있다
		return "member/joinForm"; // member 밑에 joinForm을 열어달라

	}

	@PostMapping("join")
	public String join(
			@Valid @ModelAttribute("member") BeeJoinForm beeJoinForm,
			BindingResult result) throws IOException {

		// 이메일 인증 확인
		if (beeJoinForm.getToken() == null || beeJoinForm.getToken().isEmpty()) {
			result.reject("tokenError", "인증 토큰이 필요합니다.");
			return "/member/joinForm";
		}

		if (!jwtTokenProvider.validateToken(beeJoinForm.getToken())) {
			result.reject("tokenError", "유효하지 않거나 만료된 인증 토큰입니다.");
			return "/member/joinForm";
		}

		String emailFromToken = jwtTokenProvider.getEmailFromJWT(beeJoinForm.getToken());
		Integer verificationCodeFromToken = jwtTokenProvider.getVerificationCodeFromJWT(beeJoinForm.getToken());

		// 클라이언트에서 전달된 인증 번호와 토큰 내의 인증 번호를 비교
		String userVerificationCode = beeJoinForm.getVerificationCode();
		if (verificationCodeFromToken == null || !verificationCodeFromToken.toString().equals(userVerificationCode)) {
			result.reject("verificationError", "인증 번호가 일치하지 않습니다.");
			return "/member/joinForm";
		}

		// 이메일을 폼에서 가져온 이메일과 토큰에서 가져온 이메일이 일치하는지 확인
		if (!emailFromToken.equals(beeJoinForm.getEmail())) {
			result.reject("emailError", "이메일이 일치하지 않습니다.");
			return "/member/joinForm";
		}

		// 유효성 검사 오류 처리
		if (result.hasErrors()) {
			return "/member/joinForm";
		}

		// Email 체크 *reject : 좀더 제한을 주고싶을때
		if (beeJoinForm.getEmail().isEmpty()) { // 이메일이 비어있는지 확인
			result.reject("emailError", "이메일을 입력해주세요");
			return "/member/joinForm";
		} else if (!beeJoinForm.getEmail().contains("@")) { // @가 없는지 확인
			result.reject("emailError", "이메일 형식이 맞지 않음");
			return "/member/joinForm";
		}

		// MemberJoinForm -> Member 변환은 멤버조인폼에서하고 여기선 형식과 이름만 변환
		BeeMember beeMember = BeeJoinForm.toMember(beeJoinForm);

		// id 체크(안하면 동일한ID가있는데 다시 쓰면 덮어씌어버림)
		BeeMember findMember = beeMemberService.findMemberById(beeMember.getMember_id());
		System.out.println("findMember() 실행: " + findMember);

		// id 중복
		if (findMember != null) { // id가 있더라(중복)
			result.reject("duplicate", "아이디가 중복되었습니다");
			return "/member/joinForm"; // 회원가입 성공후 list로 보내는데 url에 안남기고
		}

		// 회원가입 진행
		beeMember.setRole(Role.USER); // 회원가입시 기본권한
		beeMember.setPassword(passwordEncoder.encode(beeMember.getPassword())); // 비밀번호 암호화

		beeMemberService.saveMember(beeMember);

		return "redirect:/"; // redirect:/url에 안남기고
	}

	// **************로그인 페이지 이동

	@GetMapping("login")
	public String loginForm(Model model) { // 빈객체를 담아 Model에 보냄
		model.addAttribute("loginForm", new BeeLoginForm()); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
		return "member/loginForm";
	}

	// *******로그아웃 처리
	@GetMapping("logout")
	public String logout(HttpServletResponse response, HttpServletRequest request) {
		// JWT 기반 로그아웃은 클라이언트 측에서 로컬에 저장된 토큰을 삭제하는 것이 일반적
		// 서버 측에서는 세션을 사용하지 않으므로, 세션 무효화는 불필요
		// 클라이언트에서 로그아웃을 처리하도록 안내
		return "redirect:/";
	}

	// ***************@@@@내정보 이동@@@********************
	@GetMapping("myInfo")
	public String myInfo(@RequestHeader("Authorization") String token, Model model) {
		if (token == null || !token.startsWith("Bearer ")) {
			return "redirect:/member/login";
		}

		// JWT 토큰에서 사용자 정보 추출
		String jwt = token.substring(7); // "Bearer " 부분 제거
		String memberId = jwtTokenProvider.getMemberIdFromJWT(jwt);

		// 데이터베이스에서 최신 회원 정보 가져오기
		BeeMember updatedMember = beeMemberService.findMemberById(memberId);
		model.addAttribute("loginMember", updatedMember); // 최신 회원 정보 전달

		return "member/myInfo"; // 마이페이지로 이동
	}


	// **************@@@@내정보 수정페이지 이동@@@@**************
	@GetMapping("updateMyInfo")
	public String goUpdateMyInfo(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		model.addAttribute("loginMember", loginMember); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고

		return "member/updateMyInfo";
	}

	// ***************@@@@내정보 수정페이지에서 수정@@@@*****************
// ***************@@@@내정보 수정페이지에서 수정@@@@*****************
	@PostMapping("updateMyInfo")
	public String update(@AuthenticationPrincipal BeeMember loginMember,
						 @Validated @ModelAttribute BeeUpdateForm beeUpdateForm, BindingResult result,
						 RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {

		// 회원 정보 업데이트
		BeeMember updatedMember = BeeUpdateForm.toBeeMember(beeUpdateForm);
		updatedMember.setMember_id(loginMember.getMember_id()); // 회원 ID 유지
		updatedMember.setBirth(loginMember.getBirth());
		updatedMember.setGender(loginMember.getGender());
		beeMemberService.updateMember(updatedMember); // 서비스 호출하여 업데이트 수행

		// 세션 업데이트 제거 (JWT 사용 시 불필요)
		// request.getSession().setAttribute("loginMember", beeMemberService.findMemberById(loginMember.getMember_id()));

		return "redirect:/member/myInfo"; // 수정 완료 후 프로필 페이지로 리다이렉트
	}


	// *******************비밀번호 변경페이지 이동****************
	@GetMapping("passwordChange")
	public String goPasswordChange(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		model.addAttribute("loginMember", loginMember); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고

		return "member/passwordChange";

	}

	// ********************비밀번호 변경페이지에서 변경***************
// ********************비밀번호 변경페이지에서 변경***************
	@PostMapping("passwordChange")
	public String passwordChange(@AuthenticationPrincipal BeeMember loginMember,
								 @Validated @ModelAttribute PasswordChange passwordChange, BindingResult result,
								 RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {

		if (result.hasErrors()) {
			// 검증 오류가 있는 경우 수정 페이지로 돌아가서 오류 메시지를 표시
			model.addAttribute("loginMember", loginMember);
			return "member/passwordChange";
		}

		// 비밀번호 확인(바꿀 비밀번호와 비밀번호 확인이 같은지)
		if (!passwordChange.getPassword().equals(passwordChange.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "error.confirmPassword", "비밀번호가 서로 다릅니다.");
			model.addAttribute("loginMember", loginMember);
			return "member/passwordChange";
		}

		// 기존 비밀번호와 일치하는지 확인 필요
		if (passwordEncoder.matches(passwordChange.getPassword(), loginMember.getPassword())) {
			model.addAttribute("samePasswordError", "새로운 비밀번호는 기존 비밀번호와 달라야 합니다.");
			return "member/passwordChange";
		}

		// 비밀번호 변경
		BeeMember updatedMember = new BeeMember();
		updatedMember.setMember_id(loginMember.getMember_id());
		updatedMember.setPassword(passwordEncoder.encode(passwordChange.getPassword())); // 암호화

		beeMemberService.updatePassword(updatedMember);

		// JWT 기반 인증에서는 토큰을 갱신할 필요가 있음
		// 클라이언트가 재로그인하도록 유도하거나 JWT를 갱신하는 방식으로 처리

		return "redirect:/member/myInfo";
	}

	// **********************내가 쓴글 이동**********************
	@GetMapping("myList")
	public String myList(@AuthenticationPrincipal BeeMember loginMember, @ModelAttribute Restaurant restaurant,
			Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		MyPage myPage = loginMember.getMyPage(); 
	    model.addAttribute("myPage", myPage);
	    return "member/myList"; 
	}

	// ***********************내가 쓴댓글 이동***************************
	@GetMapping("myReply")
	public String myReply(@AuthenticationPrincipal BeeMember loginMember, @ModelAttribute Restaurant restaurant,
			Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		// 필요한 데이터가 있으면 모델에 추가합니다.
		// model.addAttribute("data", someData);
		return "member/myReply"; // templates/member/myList.html을 반환
	}

	// **************************회원탈퇴 페이지 이동****************************
	@GetMapping("deleteMember")
	public String deleteMemberPage(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		model.addAttribute("loginMember", loginMember);
		return "member/deleteMember"; // 탈퇴 확인 페이지로 이동
	}

	// *************************회원탈퇴하기*****************************
// *************************회원탈퇴하기*****************************
	@PostMapping("deleteMember")
	public String deleteMember(@AuthenticationPrincipal BeeMember loginMember,
							   @RequestParam("password") String password, RedirectAttributes redirectAttributes) {

		// 비밀번호 확인
		if (!passwordEncoder.matches(password, loginMember.getPassword())) {
			redirectAttributes.addFlashAttribute("errorMessage", "패스워드가 틀립니다.");
			return "redirect:/member/deleteMember";
		}

		// 회원 삭제 전에 관련된 방문 기록 삭제
		myPageVisitRepository.deleteByVisitor(loginMember);

		// 회원 삭제
		beeMemberService.deleteMember(loginMember.getMember_id());

		// 세션 무효화 대신 클라이언트 측에서 토큰 삭제 안내
		// 클라이언트 측에서 로컬 스토리지의 JWT 토큰 삭제 유도

		return "redirect:/";
	}




}
