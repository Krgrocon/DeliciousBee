package com.example.deliciousBee.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.deliciousBee.model.member.BeeJoinForm;
import com.example.deliciousBee.model.member.BeeLoginForm;
import com.example.deliciousBee.model.member.BeeMember;
import com.example.deliciousBee.model.member.BeeUpdateForm;
import com.example.deliciousBee.service.BeeMemberService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller //응답 html
@RequestMapping("member")
public class MemberController {
	
//	@Autowired  //필드주입방식 의존성주입
	private BeeMemberService beeMemberService;
	
	@Autowired //세터를이용한 의존성주입
	public void setBeeMemberService(BeeMemberService beeMemberService) {
		this.beeMemberService = beeMemberService;
		
	}
	
	
//**************회원가입 페이지 이동*************
	@GetMapping("join")
	public String joinForm(Model model) {
		model.addAttribute("member", new BeeJoinForm());   //new Member(): 빈객체를 담아서 보낸다-> member가가지고있는 필드이름으 활용할수있다
		return "member/joinForm"; //member 밑에 joinForm을 열어달라
		
	}
	
	//*************회원가입
		@PostMapping("join")       // @ModelAttribute("member") : 담을때 "member"라고담아라, 안그러면 MemberJoinFOrm으로 담아서 joinForm에서 못찾음
		public String join(@Validated @ModelAttribute("member") BeeJoinForm beeJoinForm //사용자가 회원가입할 정보 :member 로 날려옴, @Validated: member를 유효성체크 하겠다
				, BindingResult result) { //BindingResult result: try catch 처럼 예외 잡아줌 예외가발생했어도 프로그램계속되라
			              //@Validated를 쓸꺼면 BindingResult같이 써야됨
			if (result.hasErrors()) {
				//예외 발생

				return "/member/joinForm"; //회원가입 폼으로 다시보낸다
			}
			
			//Email 체크  *reject : 좀더 제한을 주고싶을때
			if(!beeJoinForm.getEmail().contains("@"))  { //@가 없으면, !:반대 
				result.reject("emailError", "이메일 형식이 맞지 않네"); //에러코드와 에러메시지 입력할수있따
				return "/member/joinForm";
		    }
			
		
			//MemberJoinForm -> Member 변환은 멤버조인폼에서하고 여기선 형식과 이름만 변환
			BeeMember beeMember = BeeJoinForm.toMember(beeJoinForm);
			
			
	//id 체크(안하면 동일한ID가있는데 다시 쓰면 덮어씌어버림)
			BeeMember findMember = beeMemberService.findMemberById(beeMember.getMember_id());
			log.info("findMember() 실행: {}", findMember);
			
	//id 중복
			if(findMember != null) {  //id가 있더라(중복)
				result.reject("duplicate", "이이디가 중복되었습니다");
				return "/member/joinForm"; //회원가입 성공후 list로 보내는데 url에 안남기고
			}
	//회원가입 진행
			beeMemberService.saveMember(beeMember); 
			return "redirect:/";  // redirect:/url에 안남기고
		}
		
		
	//**************로그인 페이지 이동

		@GetMapping("login")
		public String loginForm(Model model) { //빈객체를 담아 Model에 보냄
			model.addAttribute("loginForm", new BeeLoginForm()); //LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
			return "member/loginForm";  
			}
		
	//****************로그인 처리
		@PostMapping("login")                      //defaultValue: 없없을때 어디로가라
		public String login(@RequestParam(name="redirectURL", defaultValue = "/") String redirectURL //
							, @Validated @ModelAttribute("loginForm")  BeeLoginForm loginForm
							, BindingResult result //@Validated BindingResult(에러가나도 예외처리하는것처럼 화이트라벨 안뜨게해줌) 두개 세트
							, HttpServletRequest request) { // HttpServletRequest : 세션 
			//log.info("loginForm: {}", loginForm);
			
			if(result.hasErrors()) { //hasErrors발생시키고 loginForm의 문구남겨줌 
				return "member/loginForm";
			}
			
	//로그인 검증
			//1. DB가서 id가 member 테이블에 있는지, 없다-> reject()
			BeeMember findMember = beeMemberService.findMemberById(loginForm.getMember_id());
			
			if(findMember == null) {
				result.reject("noID", "아이디가 존재하지 않습니두");
				return "member/loginForm";
			}
			//2.있다-> 그아이디가 가지고있는 password와 입력받은 password가 같은지 확인, 다르다->reject()
			if(!findMember.getPassword().equals(loginForm.getPassword())) { //!: 반대 -> 비번이다르면
				result.reject("notPassword", "패스워드 틀림");
				return "member/loginForm";
			}
			//세션을 이용한 로그인
			//세션: 웹브라우저와 서버 사이에 생성된 데이터로 [서버 사이드]에저장
					HttpSession session = request.getSession(true);
					session.setAttribute("loginMember", findMember); //findMember 유효성검사 다한 객체자체를 담을수있음
					
					// 리다이렉션 URL 검증
				    if (redirectURL == null || redirectURL.isEmpty()) {
				        redirectURL = "/";
				    }
					
					return "redirect:" + redirectURL; //로그인성공한다면 너가원래 가려고했던곳으로 보내주겠따
				}
				
				@GetMapping("sessioninfo")
				public String sessionInfo(HttpServletRequest request) {
					//getSession()의 파라미터를 비워놓으면 세션이 없는경우 만들어버린다,
					//false: 세션이 없으면 null을 리턴
					HttpSession session = request.getSession(false); 
					if(session == null) {
						return "redirect:/member/login";
					}
					log.info("sessionId: {}", session.getId());
					log.info("maxInactiveInterval: {}", session.getMaxInactiveInterval());
					log.info("creationTime: {}", new Date(session.getCreationTime()));
					log.info("lastAccessedTime: {}", new Date(session.getLastAccessedTime()));
					
					
					
					return "redirect:/";
				}
				
			//*******로그아웃 처리
				@GetMapping("logout")
				public String logout(HttpServletResponse response
									, HttpServletRequest request) {
					//쿠키 로그아웃
					//쿠키삭제는 새로운걸 덮어씌우는 방식밖에없다,-> 전과 같은 이름을 만들어서 값을 null로 대체
//					Cookie cookie = new Cookie("cookieLoginId", null);
//					cookie.setPath("/");
//					cookie.setMaxAge(0); //쿠기 살아있는 시간 0-> 만들자마자 사라짐
//					response.addCookie(cookie);
					
					
					//세션 로그아웃(2가지방법)
					HttpSession session = request.getSession();
					//1.쿠키처럼 업는걸덮어씌우기
					session.setAttribute("loginMember", null);
					//2.일괄적으로 세션값을 리셋
					session.invalidate();
					
					return "redirect:/";  //보내도 쿠키가 살아있음
				}
			
				
//***************마이페이지 이동********************
			@GetMapping("myPage")
			public String myInfo(@SessionAttribute(name="loginMember") BeeMember loginMember
								, Model model) {
				if(loginMember == null) {			        
			        return "redirect:/login";
			    }
				
				model.addAttribute("loginMember", loginMember); //LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
				return "member/myPage";  
				}
			
//**************수정페이지 이동**************			
			@GetMapping("updateMyInfo")
			public String goUpdate(@SessionAttribute(name="loginMember", required = false) BeeMember loginMember
								      , Model model) {
				model.addAttribute("loginMember", loginMember); //LoginForm()의 빈객체를 담아 보내줌, 필드를 활용하려고
				
				return "member/updateMyInfo";
			}


//***************수정페이지에서 수정*****************
			@PostMapping("updateMyInfo")
			public String update(@Validated @ModelAttribute BeeUpdateForm beeUpdateForm
									, BindingResult result) {
				if(result.hasErrors()) {
					return "/";
				}
				
				BeeMember updateBeeMember =  BeeUpdateForm.toBeeMember(beeUpdateForm);
				 try {
				        beeMemberService.updateMember(updateBeeMember);
				    } catch (EntityNotFoundException e) {
				        result.reject("error.member_not_found", e.getMessage());
				        return "updateForm"; // 수정 페이지로 돌아갑니다.
				    }

				return "redirect:/updateMyInfo";
			}
}
