package com.shopme.site.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestRepository {

	@Autowired
	private CustomerService service;
	
	@PostMapping("/customers/check_unique_email")
	public String checkUniqueEmail(String email) {
		Boolean isUnique=service.isEmailUnique(email);
		if(isUnique) {
			return "OK";
		}
		
		return "Duplicated";
	}
}
