package com.infosys.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.infosys.DAO.UserRepo;
import com.infosys.Entity.User;
import com.infosys.services.UserService;
import com.infosys.services.UserServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomFailureHandler extends SimpleUrlAuthenticationFailureHandler{

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepo userRepo;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String email = request.getParameter("username");
		User user = userRepo.findByEmail(email);
		
		if(user!=null) {
			if(user.isEnable() ) {
				if(user.isAccountNonLocked()) {
					if(user.getFailedAttempts() < UserServiceImpl.ATTEMPT_LIMIT-1) {
						userService.increaseFailedAttempt(user);
					} else {
						userService.lockAccount(user);
						exception = new LockedException("Your account is locked! failed attempts 3");
					}
				} 
				else {
					
					if(userService.unlockAccountTimeExpired(user)) {
						exception = new LockedException("Account is unlocked! Pls try to login");
					} else {
						exception = new LockedException("Account is locked! Pls try again after sometime...");
					}
				}
				
			} 
			else {
				exception = new LockedException("Account is inactive... verify account");
			}
		}
		super.setDefaultFailureUrl("/signin?error");
		super.onAuthenticationFailure(request, response, exception);
	}

}
