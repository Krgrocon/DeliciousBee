package com.example.deliciousBee.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

//Filter: 다음으로 가도되는지 확인하는 클래스 , 처음이자 마지막 필터면 컨트롤러로감
@Slf4j            
public class LogFilter implements Filter { //implements Filter: 구현하면 필터준비완료
							//HttpServletRequest,	//HttpServletResponse 의 부모
	@Override //반드시 구현해야됨 , |ServletRequest, ServletResponse: 파라미터에 뭐가들었는지 컨트롤러 가기전에 볼수있따
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {          //filterchain:다음으로 넘기기
		log.info("logFilter의 doFilter() 실행");
		HttpServletRequest httpRequest = (HttpServletRequest)request; //형변환(다형성) 부모->자식 
		String requestURI = httpRequest.getRequestURI(); //요청값(uri) 자체를 가져올수있다, 어느페이지를 많이가는지 알수있따
		
//		log.info("{}", requestURI);  
		
		
		chain.doFilter(request, response); //필터가 하나면 컨트롤러한테 준다, 2개면 다음 우선순위한테 넘김
	}
	
	@Override //필수 아님
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Log Filter Init");
	}
	
	@Override //필수 아님, 서버가 꺼질때 실행되는 메서드
	public void destroy() {
		log.info("Log Filter Destory"); 
	}
}
