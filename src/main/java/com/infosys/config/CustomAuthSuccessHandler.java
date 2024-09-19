package com.infosys.config;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.infosys.Entity.User;
import com.infosys.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler{

	@Autowired
	private UserService userService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	 Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		CustomUser customUser = (CustomUser) authentication.getPrincipal(); //authentication.getPrincipal(): This method retrieves the principal (i.e., the currently authenticated user) from the Authentication object. The principal represents the identity of the user, typically an instance of a class that implements UserDetails. In your case, this will be an instance of your custom CustomUser class
		
		User user = customUser.getUser();
		
		if(user != null) {
			userService.resetAttempt(user.getEmail());
		}
		
		if(roles.contains("ROLE_ADMIN")) {
			response.sendRedirect("/admin/profile");
		} else  {
			response.sendRedirect("/user/profile");
		}
	}
}
