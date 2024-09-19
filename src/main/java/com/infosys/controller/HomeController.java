package com.infosys.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.infosys.DAO.UserRepo;
import com.infosys.Entity.User;
import com.infosys.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepo userRepo;

	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/register")
	public String register () {
		return "register";
	}
	
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@ModelAttribute  // for preparing model data before every request
	public void commonUser(Principal p, Model m) {
		if(p!=null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);	
			m.addAttribute("userlogged", user);
		} else {
			m.addAttribute("userlogged", null);
		}

	}
	
//	@GetMapping("/user/profile")
//	public String profile(Principal p, Model m) { //In Spring Security, the principal refers to the currently authenticated user or entity
//		String email = p.getName();
//		User user = userRepo.findByEmail(email);
//		m.addAttribute("user", user);
//		return "profile";
//	}
//	
//	@GetMapping("/user/home")
//	public String home() {
//		return "home";
//	}
//	
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute User user, HttpSession session, Model m, HttpServletRequest request) {

		System.out.println("USER = " + user.toString());
		String url = request.getRequestURL().toString(); 
//		System.out.println(url); //  http://localhost:8081/saveUser
		
		// we have to make it as by passing
		// http://localhost:8081/verify?code=fqfl xtnj yzuq dplk
		String repl = request.getServletPath(); 
//		System.out.println(repl); // /saveUser
		
		url = url.replace(repl, ""); 
//		System.out.println(url); // http://localhost:8081
		
		User existsUser = userRepo.findByEmail(user.getEmail());
		if(existsUser!=null) {
			session.setAttribute("err", "User email already exists!");
			return "redirect:/register";
		}
		User u = userService.saveUser(user, url);
		if(u!=null) {
//			System.out.println("Save Success");
			session.setAttribute("msg", "Register Successfully!");
		} else {
//			System.out.println("Error in server");
			session.setAttribute("err", "Something Wrong in Server");
		}
		return "redirect:/register";
	}
	
	@GetMapping("/verify")
	public String verify(@RequestParam String code,Model m) {
		boolean verified = userService.verifyAccount(code);
		if(verified) {
			m.addAttribute("verificationMsg", "Your Account is Verified Successfully!, Log in to access services...");
			
		} else {
			m.addAttribute("verificationMsg", "Maybe your verification code is incorrect or already verified...");
		}
		return "verify_success";
	}
	

	
	
}
