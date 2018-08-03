package com.shattered.baxt;

import java.security.Principal;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/login")
public class LoginController {

	@RequestMapping("/")
	public String showIndex(Model model, Principal principal, @RequestParam(value = "code", required = false) String code) {	
		model.addAttribute("message", CodeRepo.getMessage(code));
		model.addAttribute("principal", principal); 
		return "index" ;
	}

	@RequestMapping("/login")
	public String loginPage(Model model, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		if(	SecurityContextHolder.getContext().getAuthentication() != null &&
				SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
				!(SecurityContextHolder.getContext().getAuthentication() 
						instanceof AnonymousAuthenticationToken) ) {
			model.addAttribute("message", CodeRepo.getMessage("already-auth"));
			return "/";
		}
		return "login";
	}

	@RequestMapping("/logout")
	public void logout(Model model, HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "code", required = false) String code){
		model.addAttribute("message", CodeRepo.getMessage(code));
	
	}

	@GetMapping("/signup")
	public String signupShow(Model model, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		return "signup";
	}

	@PostMapping("/signup")
	public String signupSubmit(Model model, @RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("passConf") String passConf, @RequestParam("email") String email, @RequestParam(value = "code", required = false) String code) {
		model.addAttribute("message", CodeRepo.getMessage(code));
		ArrayList<String> errors = ValidationService.validateNewUserInfo(username, password, passConf, email) ;
		if(errors.size() > 0) {
			model.addAttribute("message", CodeRepo.getMessage("bad-creation-info"));
			int count = 0 ;
			for ( String ec : errors) { 
				model.addAttribute("message" + count, CodeRepo.getMessage(ec));
				count++;
			}
			return "signup" ;
		}
		MyUserDetailsService.createNewUser(username, password, email) ;
		UploadController.checkUserDir(username);
		model.addAttribute("message", CodeRepo.getMessage("good-creation-info"));
		return "login" ; 		
	}
}















