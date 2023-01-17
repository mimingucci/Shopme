package com.shopme.site.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestRepository {

	
	private CustomerService service;
	
	@Autowired
	public CustomerRestRepository(@Lazy CustomerService customerService) {
		this.service=customerService;
	}
	
	@PostMapping("/customers/check_unique_email")
	public String checkUniqueEmail(String email) {
		Boolean isUnique=service.isEmailUnique(email);
		if(isUnique) {
			return "OK";
		}
		
		return "Duplicated";
	}
}
