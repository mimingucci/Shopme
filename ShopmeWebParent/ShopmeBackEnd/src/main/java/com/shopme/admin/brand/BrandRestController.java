package com.shopme.admin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {
  
	@Autowired
	private BrandService service;
	
	@PostMapping("/brands/check_unique")
	public String checkUnique(Integer id, String name) {
		return service.checkUnique(id, name);
	} 
}
