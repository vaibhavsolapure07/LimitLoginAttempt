package com.infosys.services;


import com.infosys.Entity.User;

public interface UserService {

	public User saveUser(User u, String url);
	
	public void removeSessionMessage();
	
	public void sendEmail(User user, String url);
	
	public boolean verifyAccount(String verificationCode);
	
	public void increaseFailedAttempt(User user);
	
	public void resetAttempt(String email);
	
	public void lockAccount(User user);
	
	public boolean unlockAccountTimeExpired(User user);
}
