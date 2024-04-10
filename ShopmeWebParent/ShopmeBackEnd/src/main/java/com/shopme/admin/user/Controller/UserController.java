package com.shopme.admin.user.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.setting.SettingRepository;
import com.shopme.admin.user.UserService;
import com.shopme.common.Constants;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
public class UserController {
	private String defaultRedirectURL = "redirect:/users/page/1?sortField=firstName&sortDir=asc";
	@Autowired
	private SettingRepository repo;
	
	@Autowired
	private UserService service;
	
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create New User");
		
		return "user/user_form";
	}
	
	@GetMapping("/users")
	public String users(Model model) {
		List<User> listUsers=service.listAll();
		model.addAttribute("listUsers", listUsers);
		return "/user/users";
	}
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			
			String uploadDir = "user-photos/" + savedUser.getId();
		    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
//			AmazonS3Util.removeFolder(uploadDir);
//			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());			
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
		
		return "redirect:/users";
	}
	
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum,
			@RequestParam(value = "sortField", defaultValue = "") String sortField,
			@RequestParam(value = "sortDir", defaultValue = "") String sortDir
			, Model model) {
		Sort sort=null;
		Iterable<User> listByPage=null;
		if(sortField.length()>0 && sortDir.length()>0) {
			if(sortDir=="asc") {
			    sort=Sort.by(sortField).ascending();
			}else {
				sort=Sort.by(sortField).descending();
			}
		}
		if(sort==null) {
		    listByPage=service.listByPage(pageNum);
		}else {
			listByPage=service.listBySortedPage(pageNum, sort);
		}
		List<User> listUsers=new ArrayList<>();
		for(User user : listByPage) {
			listUsers.add(user);
		}
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("currentPage", pageNum);
		if(sortDir.length()==0) {
			model.addAttribute("sortDir", "asc");
		}else {
			if(sortDir.equals("asc")) {
				model.addAttribute("sortDir", "des");
			}else {
				model.addAttribute("sortDir", "");
			}
		}
		return "/user/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The user ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return defaultRedirectURL;
	}
	
}
