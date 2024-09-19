package com.infosys.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import static org.springframework.security.config.Customizer.withDefaults;
import com.infosys.Entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private CustomAuthSuccessHandler customAuthSuccessHandler;

	@Autowired
	private CustomFailureHandler customFailureHandler;
	
	@Bean
	public User user() {
		return new User();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new CustomUserDetailsService();
	}
	
	@Bean
	public DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http.csrf(csrf->csrf.disable())
//			.authorizeHttpRequests(requests -> requests.requestMatchers("/", "/register", "/saveUser", "/signin").permitAll()
//					.requestMatchers("/user/**").authenticated())
////					.anyRequest().permitAll()) // to allow other requests
//			.formLogin(form->form.loginPage("/signin")
//					.loginProcessingUrl("/userlogin")
////					.usernameParameter("email")
//					.defaultSuccessUrl("/user/profile").permitAll());
////			.logout(out->out.logoutSuccessUrl("/").permitAll());
		
		http.csrf(csrf->csrf.disable())
		.authorizeHttpRequests(requests -> requests.requestMatchers("/user/**").hasRole("USER")
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.requestMatchers("/**").permitAll())
		.formLogin(form->form.loginPage("/signin")
				.loginProcessingUrl("/userlogin")
				.failureHandler(customFailureHandler)
				.successHandler(customAuthSuccessHandler).permitAll())
			.logout(withDefaults());
		return http.build();
	}
}
