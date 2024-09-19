package com.infosys.services;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.infosys.DAO.UserRepo;
import com.infosys.Entity.User;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
@Service
@Transactional
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepo userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JavaMailSender mailSender;
	
//	private static final long lock_duration_time=24 * 60 * 60 * 1000; // lock for 24hr
	private static final long lock_duration_time=60000;
	public static final long ATTEMPT_LIMIT=3;

	
	@Override
	public User saveUser(User u, String url) {
		// TODO Auto-generated method stub
		String password = passwordEncoder.encode(u.getPassword());
		u.setPassword(password);
		u.setRole("ROLE_USER");
		u.setEnable(false);
		u.setVerificationCode(UUID.randomUUID().toString());
		
		u.setAccountNonLocked(true);
		u.setFailedAttempts(0);
		u.setLockTime(null);
		User newUser=  userRepo.save(u);
		
		if(newUser!=null) {
			sendEmail(newUser, url);
//			boolean verified = verifyAccount(url);
		}
		
		return newUser;
	}


	@Override
	public void removeSessionMessage() {
		// TODO Auto-generated method stub
		HttpSession session= ((ServletRequestAttributes)(RequestContextHolder.getRequestAttributes())).getRequest().getSession();
		session.removeAttribute("msg");
		session.removeAttribute("err");
	}


	@Override
	public void sendEmail(User user, String url) {
		// TODO Auto-generated method stub
		String from ="ditpimprivaibhav@gmail.com";
		String to = user.getEmail();
		String subject = "Account Verfication";
		String content =  "Dear [[name]], <br><br>" +
                "Thank you for registering with us! To get started, please verify your email address by clicking the link below:<br><br>" +
                "<h3><a href = \"[[URL]]\" target=\"_self\">VERIFY</a></h3>" +
                "<br>We're here to help you have the best experience. If you have any questions or didn't request this, feel free to contact us.<br><br>" +
                "<img src=\"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT09bkIzT8NTSQuIDRShHnR7oernKU3CIUpSQ&s\" alt= \"ImageVSS\" height=\"150px\" width=\"250px\" style=\"border-radius:10px;\">" +
                "<br><br>Best regards,<br>" +
                "The VSS Team";
		
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
			
			helper.setFrom(from, "VS");
			helper.setTo(to);
			helper.setSubject(subject);
			
			content = content.replace("[[name]]", user.getName());
			String siteUrl  = url + "/verify?code=" + user.getVerificationCode();
			
			content = content.replace("[[URL]]", siteUrl);
			helper.setText(content, true); // this true is for html
			mailSender.send(mimeMessage);
					
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}


	@Override
	public boolean verifyAccount(String verificationCode) {
		// TODO Auto-generated method stu
		User u = userRepo.findByVerificationCode(verificationCode);
		if(u!=null) {
			u.setEnable(true);
			u.setVerificationCode(null);
			userRepo.save(u);
			return true;
			
		}
		return false;
	}


	@Override
	public void increaseFailedAttempt(User user) {
		// TODO Auto-generated method stub
		int attempt = user.getFailedAttempts()+1;
		userRepo.updateFailedAttempt(attempt, user.getEmail());
		
	}


	@Override
	public void resetAttempt(String email) {
		// TODO Auto-generated method stub
		userRepo.updateFailedAttempt(0, email);
		
	}


	@Override
	public void lockAccount(User user) {
		// TODO Auto-generated method stub
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepo.save(user);
	}


	@Override
	public boolean unlockAccountTimeExpired(User user) {
		// TODO Auto-generated method stub
		
		// unlock after 1 min
		
		long lockTimeInMills = user.getLockTime().getTime();
		long currentTimeMills = System.currentTimeMillis();
		if(lockTimeInMills + lock_duration_time < currentTimeMills) {
			user.setAccountNonLocked(true);
			user.setLockTime(null);
			user.setFailedAttempts(0);
			userRepo.save(user);
			return true;
		}
		
		return false;
		
	}
	
	

}
