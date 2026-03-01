package com.lsj.demo;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import com.lsj.demo.interceptor.BeforeActionInterceptor;
import com.lsj.demo.interceptor.NeedLoginInterceptor;
import com.lsj.demo.interceptor.NeedLogoutInterceptor;

@Configuration
public class WebMvcConfigurer implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {

	// BeforeActionInterceptor 연결
	@Autowired
	BeforeActionInterceptor beforeActionInterceptor;

	// NeedLoginInterceptor 연결
	@Autowired
	NeedLoginInterceptor needLoginInterceptor;

	// NeedLogoutInterceptor 연결
	@Autowired
	NeedLogoutInterceptor needLogoutInterceptor;
	
	// 등록
	@Value("${app.frontend.allowed-origins:http://localhost:3000,http://localhost:5173}")
	private String allowedOrigins;

	// 웹 연동을 위한 코드
	@Override
	public void addCorsMappings(CorsRegistry registry) {
	    String[] origins = Arrays.stream(allowedOrigins.split(","))
	            .map(String::trim)
	            .filter(s -> !s.isEmpty())
	            .map(s -> s.endsWith("/") ? s.substring(0, s.length() - 1) : s) // 슬래시 제거
	            .toArray(String[]::new);

	    registry.addMapping("/api/**")
	            .allowedOrigins(origins)
	            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD")
	            .allowedHeaders("*")
	            .allowCredentials(true)
	            .maxAge(3600);
	}


	// 등록
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
//		registry.addInterceptor(beforeActionInterceptor).addPathPatterns("/**");// 모든 요청이 들어오기 전에
//																				// beforeactioninterceptor를 활용 하겠다.
//
//		registry.addInterceptor(needLoginInterceptor).addPathPatterns("/usr/article/write")
//				.addPathPatterns("/usr/article/doWrite").addPathPatterns("/usr/article/modify")
//				.addPathPatterns("/usr/article/doModify").addPathPatterns("/usr/article/doDelete")
//				.addPathPatterns("/usr/member/doLogout");
//
//		registry.addInterceptor(needLogoutInterceptor).addPathPatterns("/usr/member/login")
//				.addPathPatterns("/usr/member/doLogin").addPathPatterns("/usr/member/join")
//				.addPathPatterns("/usr/member/doJoin");
		InterceptorRegistration ir;

		ir = registry.addInterceptor(beforeActionInterceptor);
		ir.addPathPatterns("/**");
		ir.addPathPatterns("/favicon.ico");
		ir.excludePathPatterns("/resource/**");
		ir.excludePathPatterns("/error");
		ir.excludePathPatterns("/api/**");

		ir = registry.addInterceptor(needLoginInterceptor);
		ir.addPathPatterns("/usr/article/write");
		ir.addPathPatterns("/usr/article/doWrite");
		ir.addPathPatterns("/usr/article/modify");
		ir.addPathPatterns("/usr/article/doModify");
		ir.addPathPatterns("/usr/article/doDelete");
		ir.addPathPatterns("/usr/member/doLogout");

		ir.addPathPatterns("/usr/reactionPoint/doGoodReaction");
		ir.addPathPatterns("/usr/reactionPoint/doBadReaction");

		ir = registry.addInterceptor(needLogoutInterceptor);
		ir.addPathPatterns("/usr/member/login");
		ir.addPathPatterns("/usr/member/doLogin");
		ir.addPathPatterns("/usr/member/join");
		ir.addPathPatterns("/usr/member/doJoin");

	}
}