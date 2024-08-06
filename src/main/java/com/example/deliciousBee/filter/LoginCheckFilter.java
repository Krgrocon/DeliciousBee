package com.example.deliciousBee.filter;

import java.io.IOException;
import java.net.http.HttpRequest;

import org.springframework.util.PatternMatchUtils;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckFilter implements Filter{
	//필터는 어노테이션 못쓴다(서블릿영역 이라)
	@Override            // 세션                      //쿠키
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info("로그인체크필터실행");
		
		//형변환
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		
		String requestURI = httpRequest.getRequestURI();
		
		//로그인 해야 들어갈수있는 URL이냐?
		if(isLoginCheckPath(requestURI)) { //true가들어오면 체크해야되서 if문실행
			HttpSession session = httpRequest.getSession(false); //주의사항: 세션이없으면 만들어버린다 -> 만들지 않다고 false 붙이면 없으면 null
			
			if(session == null || session.getAttribute("loginMember") == null) {
				log.info("로그인하지않은사용자의요청");
				//로그인 하지 않은상태
				//서블릿에서 리다이렉트 하는 방식
				httpResponse.sendRedirect("/member/login"); //여기는 추상메서드 구간 void라 리턴불가그래서 이방식으로 
				return; //안해주면 밑에 chain.doFilter로넘어감
			}
		}
		
		chain.doFilter(request, response);
	} 
	
	private boolean isLoginCheckPath(String requestURI) { 
		//로그인을 안해도 갈수있는 경로
		String[] whiteList = {"/", "/member/login", "/member/join", "/default.css"};
		return !PatternMatchUtils.simpleMatch(whiteList, requestURI); //위에 경로가 들어오면 체크안해도된다, false 반환
		
	}
}
