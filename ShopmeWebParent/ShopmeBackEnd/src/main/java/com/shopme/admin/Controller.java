package com.shopme.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.shopme.admin.user.UserRepository;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@org.springframework.stereotype.Controller
public class Controller {
	
  @Autowired
  private UserService userService;
  
  @Autowired
  private UserRepository userRepo;
	
  @GetMapping("/test")
  public String homePage(Model model) {
	  model.addAttribute("text", "Hello");
	  List<Role> roles=userService.listRoles();
	  System.out.println(roles);
	  return "index";
  }
  
  @GetMapping("/nav")
  public String navigate() {
	  return "navigation";
  }
  
  @GetMapping("/user_form")
  public String createNewUser() {
	  return "user/user_form";
  }
  @GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "login";
		}
		
		return "redirect:/";
	}
}
