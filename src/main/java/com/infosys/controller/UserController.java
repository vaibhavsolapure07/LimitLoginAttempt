package com.infosys.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.infosys.DAO.UserRepo;
import com.infosys.Entity.User;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepo userRepo;
	
	
	@ModelAttribute  // for preparing model data before every request
	public void commonUser(Principal p, Model m) {
		if(p!=null) {
			String email = p.getName();
			User user = userRepo.findByEmail(email);	
			m.addAttribute("user", user);
		} else {
			m.addAttribute("user", null);
		}

	}


	@GetMapping("/profile")
	public String profile(){
		return "profile";
	}
}
