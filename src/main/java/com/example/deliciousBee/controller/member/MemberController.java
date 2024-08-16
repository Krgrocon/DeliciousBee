package com.example.deliciousBee.controller.member;

import java.io.IOException;

import com.example.deliciousBee.model.board.Restaurant;
import com.example.deliciousBee.model.member.*;
import com.example.deliciousBee.model.mypage.MyPage;
import com.example.deliciousBee.model.mypage.MyPageUpdateForm;
import com.example.deliciousBee.service.member.BeeMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.deliciousBee.util.FileService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller //응답 html
@RequiredArgsConstructor
@RequestMapping("member")
public class MemberController {


	private final BeeMemberService beeMemberService;
	private final HttpSession session;
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private FileService fileService; // fileStore 주입 받음.

	//**************회원가입 페이지 이동*************
	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("member", new BeeJoinForm()); // new Member(): 빈객체를 담아서 보낸다-> member가가지고있는 필드이름으 활용할수있다
		return "member/joinForm"; // member 밑에 joinForm을 열어달라

	}

	// *************회원가입
	@PostMapping("join") // @ModelAttribute("member") : 담을때 "member"라고담아라, 안그러면 MemberJoinFOrm으로 담아서
	// joinForm에서 못찾음
	public String join(@Validated @ModelAttribute("member") BeeJoinForm beeJoinForm // 사용자가 회원가입할 정보 :member 로 날려옴,
					   // @Validated: member를 유효성체크 하겠다
			, BindingResult result) throws IOException { // BindingResult result: try catch 처럼 예외 잡아줌 예외가발생했어도 프로그램계속되라
		// @Validated를 쓸꺼면 BindingResult같이 써야됨
		if (result.hasErrors()) {
			// 예외 발생

			return "/member/joinForm"; // 회원가입 폼으로 다시보낸다
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
		log.info("findMember() 실행: {}", findMember);

		// id 중복
		if (findMember != null) { // id가 있더라(중복)
			result.reject("duplicate", "이이디가 중복되었습니다");
			return "/member/joinForm"; // 회원가입 성공후 list로 보내는데 url에 안남기고
		}

		// 회원가입 진행

		beeMember.setRole(Role.USER);//회원가입시 기본권한
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
		// 쿠키 로그아웃
		// 쿠키삭제는 새로운걸 덮어씌우는 방식밖에없다,-> 전과 같은 이름을 만들어서 값을 null로 대체
//					Cookie cookie = new Cookie("cookieLoginId", null);
//					cookie.setPath("/");
//					cookie.setMaxAge(0); //쿠기 살아있는 시간 0-> 만들자마자 사라짐
//					response.addCookie(cookie);

		// 세션 로그아웃(2가지방법)
		HttpSession session = request.getSession();
		// 1.쿠키처럼 업는걸덮어씌우기
		session.setAttribute("loginMember", null);
		// 2.일괄적으로 세션값을 리셋
		session.invalidate();

		return "redirect:/"; // 보내도 쿠키가 살아있음
	}

	//**************마이페이지 이동******************
	@GetMapping("myPage")
	public String myPage(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		if (loginMember == null) {
			return "redirect:/login";
		}

//		// 조회수 증가
//		int currentViewCount = loginMember.getViewCount();
//		loginMember.setViewCount(currentViewCount + 1);

		// 업데이트된 조회수를 저장
//				beeMemberService.updateMember(loginMember);

		model.addAttribute("loginMember", loginMember); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
		return "member/myPage";
	}

	//**********************마이페이지 수정하기 페이지 이동****************************
	@GetMapping("goUpdateMyPage")
	public String goUpdateMyPage(@AuthenticationPrincipal BeeMember loginMember
			, Model model) {
		model.addAttribute("loginMember", loginMember); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
		return "member/updateMyPage";
	}

	//**********************마이페이지에서 수정하기*********************
	@PostMapping("updateMyPage")
	public String updateMyPage(@AuthenticationPrincipal BeeMember loginMember,
							   @Validated @ModelAttribute("myPageUpdateForm") MyPageUpdateForm myPageUpdateForm,
							   @RequestParam(name ="file", required = false) MultipartFile file,
							   BindingResult result,
							   Model model) {

		if (result.hasErrors()) {
			// 검증 오류가 있는 경우 수정 페이지로 돌아가서 오류 메시지를 표시
			model.addAttribute("loginMember", loginMember);
			return "member/updateMyPage"; // 수정 페이지로 돌아가기
		}
		// 첨부파일이 있는경우
		log.info("myPageUpdateForm:{}", myPageUpdateForm);
		MyPage myPage = myPageUpdateForm.toMyPage(myPageUpdateForm);
//		if (!file.isEmpty()) { // file 이없어도 값을 주기때문에 =! null 이걸로는 못거름, 비어있지않으면: !.isEmpty
//			// 첨푸파일을 서버에 저장
//						AttachedFile attachedFile = fileService.saveFile(file);
//						// 첨부파일을 저장할 리스트 생성
//						List<AttachedFile> attachedFileList = new ArrayList<>();
//						attachedFileList.add(attachedFile);
//						myPage.setAttachedFiles(attachedFileList); // 리스트를 myPage에 설정
//						myPage.setBeeMember(loginMember);
//						// 위에file이없으면 null을주니깐 null.setBoard라고줘서 에러터짐
//						// 글과 첨부파일을 DB에 저장
//						beeMemberService.saveMyPage(myPage, attachedFile);
//						return "redirect:/member/myPage";
//
//					}
//
//					// 첨부파일이 없는경우
//					beeMemberService.saveMyPage(myPage, null); // 글만 넘김
//
		return "redirect:/member/myPage";

	}

	//***************@@@@내정보 이동@@@********************
	@GetMapping("myInfo")
	public String myInfo(@AuthenticationPrincipal BeeMember loginMember, Model model) {
		if (loginMember == null) {
			return "redirect:/login";
		}

		model.addAttribute("loginMember", loginMember); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
		return "member/myInfo";
	}

	//**************@@@@내정보 수정페이지 이동@@@@**************
	@GetMapping("updateMyInfo")
	public String goUpdate(@AuthenticationPrincipal BeeMember loginMember,
						   Model model) {
		model.addAttribute("loginMember", loginMember); // LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고

		return "member/updateMyInfo";
	}

	//***************@@@@내정보 수정페이지에서 수정@@@@*****************
	@PostMapping("updateMyInfo")
	public String update(@AuthenticationPrincipal BeeMember loginMember,
						 @Validated @ModelAttribute BeeUpdateForm beeUpdateForm, BindingResult result,
						 RedirectAttributes redirectAttributes, HttpServletRequest request, Model model) {
		if (result.hasErrors()) {
			// 검증 오류가 있는 경우 수정 페이지로 돌아가서 오류 메시지를 표시
			model.addAttribute("loginMember", loginMember);
			return "member/updateMyInfo"; // 수정 페이지로 돌아가기
		}

		// 비밀번호 확인
		if (!beeUpdateForm.getPassword().equals(beeUpdateForm.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "error.confirmPassword", "비밀번호가 서로 다릅니다.");
			model.addAttribute("loginMember", loginMember);
			return "member/updateMyInfo";
		}

		// 비밀번호 길이 검증
		if (beeUpdateForm.getPassword().length() < 4 || beeUpdateForm.getPassword().length() > 20) {
			result.rejectValue("password", "error.passwordLength", "비밀번호는 4~20자여야 합니다.");
			model.addAttribute("loginMember", loginMember);
			return "member/updateMyInfo";
		}

		// 기존 비밀번호와 일치하는지 확인 필요
		if (beeMemberService.isPasswordSame(loginMember.getMember_id(), beeUpdateForm.getPassword())) {
			redirectAttributes.addFlashAttribute("errorSamePassword", "패스워드가 기존 패스워드와 같습니다.");
			model.addAttribute("loginMember", loginMember);
			return "redirect:/member/updateMyInfo";
		}

		// 회원 정보 업데이트
		BeeMember updatedMember = BeeUpdateForm.toBeeMember(beeUpdateForm);
		updatedMember.setMember_id(loginMember.getMember_id()); // 회원 ID 유지
		updatedMember.setBirth(loginMember.getBirth());
		updatedMember.setGender(loginMember.getGender());
		beeMemberService.updateMember(updatedMember); // 서비스 호출하여 업데이트 수행

		// 세션 업데이트
		HttpSession session = request.getSession();
		session.setAttribute("loginMember", updatedMember);

		return "redirect:/member/myInfo"; // 수정 완료 후 프로필 페이지로 리다이렉트
	}

	//**********************내가 쓴글 이동**********************
	@GetMapping("myList")
	public String myList(@AuthenticationPrincipal BeeMember loginMember,
						 @ModelAttribute Restaurant restaurant, Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		// 필요한 데이터가 있으면 모델에 추가합니다.
		// model.addAttribute("data", someData);
		return "member/myList"; // templates/member/myList.html을 반환
	}

	//***********************내가 쓴댓글 이동***************************
	@GetMapping("myReply")
	public String myReply(@AuthenticationPrincipal BeeMember loginMember,
						  @ModelAttribute Restaurant restaurant, Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		// 필요한 데이터가 있으면 모델에 추가합니다.
		// model.addAttribute("data", someData);
		return "member/myReply"; // templates/member/myList.html을 반환
	}

	//**************************회원탈퇴 페이지 이동****************************
	@GetMapping("deleteMember")
	public String deleteMemberPage(@AuthenticationPrincipal BeeMember loginMember,
								   Model model) {
		if (loginMember == null) {
			return "redirect:/member/login";
		}
		model.addAttribute("loginMember", loginMember);
		return "member/deleteMember"; // 탈퇴 확인 페이지로 이동
	}

	//*************************회원탈퇴하기*****************************
	@PostMapping("deleteMember")
	public String deleteMember(@AuthenticationPrincipal BeeMember loginMember,
							   @RequestParam("password") String password, HttpServletRequest request,
							   RedirectAttributes redirectAttributes) {

		// 비밀번호 확인
		if (!loginMember.getPassword().equals(password)) {
			redirectAttributes.addFlashAttribute("errorMessage", "패스워드가 틀립니다.");
			return "redirect:/member/deleteMember";
		}

		// 회원 삭제
		beeMemberService.deleteMember(loginMember.getMember_id());

		// 세션 무효화
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}

		return "redirect:/";
	}

//******************************친구 요청************************
//			 // 친구 요청 보내기
//		    @PostMapping("/member/{friendMemberId}/send-friend-request")
//		    public String sendFriendRequest(@PathVariable Long friendMemberId,
//		                                    @SessionAttribute("loginMember") BeeMember loginMember,
//		                                    RedirectAttributes redirectAttributes) {
//		        friendService.sendFriendRequest(loginMember, beeMemberService.findMemberById(friendMemberId));
//		        redirectAttributes.addFlashAttribute("message", "친구 요청을 보냈습니다.");
//		        return "redirect:/member/" + friendMemberId; // 친구 프로필 페이지로 리다이렉트
//		    }
//
//		    // 친구 요청 수락
//		    @PostMapping("/friend-request/{friendRequestId}/accept")
//		    public String acceptFriendRequest(@PathVariable Long friendRequestId,
//		                                       @SessionAttribute("loginMember") BeeMember loginMember,
//		                                       RedirectAttributes redirectAttributes) {
//		        friendService.acceptFriendRequest(friendRequestId);
//		        redirectAttributes.addFlashAttribute("message", "친구 요청을 수락했습니다.");
//		        return "redirect:/member/friends"; // 친구 목록 페이지로 리다이렉트
//		    }
//
//		    // 친구 요청 거부
//		    @PostMapping("/friend-request/{friendRequestId}/reject")
//		    public String rejectFriendRequest(@PathVariable Long friendRequestId,
//		                                       @SessionAttribute("loginMember") BeeMember loginMember,
//		                                       RedirectAttributes redirectAttributes) {
//		        friendService.rejectFriendRequest(friendRequestId);
//		        redirectAttributes.addFlashAttribute("message", "친구 요청을 거부했습니다.");
//		        return "redirect:/member/friends"; // 친구 목록 페이지로 리다이렉트
//		    }
//
//		    // 친구 목록 조회
//		    @GetMapping("/member/friends")
//		    public String getFriends(@SessionAttribute("loginMember") BeeMember loginMember,
//		                             Model model) {
//		        model.addAttribute("friends", friendService.getFriends(loginMember));
//		        return "member/friends"; // templates/member/friends.html 을 반환
//		    }
//
//
//

}
