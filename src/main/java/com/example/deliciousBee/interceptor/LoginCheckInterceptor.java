package com.example.deliciousBee.interceptor;

import java.util.Enumeration;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor  {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception { 
		String requestURI = request.getRequestURI(); //보드리스트 요청
		
		HttpSession session = request.getSession(false); //부모꺼를가지고있때문에 형변환 필요x
		
		if(session == null || session.getAttribute("loginMember") == null) {
			log.info("로그인 하지않은 사용자 요청");
			
			Enumeration<String> parameterNames = request.getParameterNames();
			
			StringBuffer stringBuffer = new StringBuffer(); 
			
			while(parameterNames.hasMoreElements()) { //has:가지고있냐, 요청이들어왔을때 request가가지고있는데 그 이름들을 getParameterNames();얘가 다가지고있음
				//true가나오면 담음
				String parameterName = parameterNames.nextElement(); //파라미터 이름을 뽑아온건 get파라미터를 하기위해서
				stringBuffer.append(parameterName + "=" + request.getParameter(parameterName) + "%26"); //append: ()안에쓴거 다 합쳐줌
			}
			
			//파라미터 1개일떄: board/read?id=3 이렇게 보내야됨 ?id=? 이걸보낼려면 2는 정해진게아님
			
			//파라미터 2개이상일때 : board/read?id=3&name=호날두
			//리다이렉트(서블릿 방식)
			response.sendRedirect("/member/login?redirectURL=" + requestURI + "?" + stringBuffer.toString());
			return false;
		}
		
		return true;
	}
}
