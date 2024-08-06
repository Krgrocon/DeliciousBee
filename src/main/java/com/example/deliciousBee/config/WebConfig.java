package com.example.deliciousBee.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.deliciousBee.filter.LogFilter;
import com.example.deliciousBee.filter.LoginCheckFilter;
import com.example.deliciousBee.interceptor.LogInterceptor;
import com.example.deliciousBee.interceptor.LoginCheckInterceptor;

import jakarta.servlet.Filter;

@Configuration //@Configuration: 필터 등록, 필터의 설정파일 , implements WebMvcConfigurer: 인터셉터 등록, 설정파일
public class WebConfig implements WebMvcConfigurer { //거쳐서 서버켜라
	
	private String[] excludePaths = {"/", "/member/join", "/member/login", "/member/logout",
									"/*.css", "/*.js", "/*.ico", "/error"};
	
	
	
	
	//필터
//	@Bean
	FilterRegistrationBean<Filter> logFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		//등록할 필터를 지정
		filterRegistrationBean.setFilter(new LogFilter());
		
		//필터의 순서적용, 숫자가 낮을 수록 먼저 실행.
		filterRegistrationBean.setOrder(1);
		
		//필터를 적용할 URL 패턴을 지정
		filterRegistrationBean.addUrlPatterns("/*");  // 최상위경로 밑으로들어오는 모든경로의 필터를적용
		
		return filterRegistrationBean;
	}
	//필터마다 Bean 등록해야된다
//	@Bean
	FilterRegistrationBean<Filter> loginCheckFilter() {
		FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
		//등록할 필터를 지정
		filterRegistrationBean.setFilter(new LoginCheckFilter());
		
		//필터의 순서적용, 숫자가 낮을 수록 먼저 실행.
		filterRegistrationBean.setOrder(2);
		
		//필터를 적용할 URL 패턴을 지정
		filterRegistrationBean.addUrlPatterns("/*");  // 최상위경로 밑으로들어오는 모든경로의 필터를적용
		
		return filterRegistrationBean;
	}
	
	
	//인터셉터
	
	@Override
		public void addInterceptors(InterceptorRegistry registry) {
		//인터셉터 등록
		registry.addInterceptor(new LogInterceptor())
		        .order(1) //인터셉터 호출 순서 지정
		        .addPathPatterns("/**") // 인터셉터를 적용할 URL 패턴지정
		        .excludePathPatterns(excludePaths); //제외할 애들
		//인터셉터 등록
		registry.addInterceptor(new LoginCheckInterceptor())
				.order(2) //인터셉터 호출 순서 지정
				.addPathPatterns("/**") // 인터셉터를 적용할 URL 패턴지정
				.excludePathPatterns(excludePaths); //제외할 애들
		}
}
