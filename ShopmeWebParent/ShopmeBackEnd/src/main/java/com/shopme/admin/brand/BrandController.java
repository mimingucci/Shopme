package com.shopme.admin.brand;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.Brand;

@Controller
public class BrandController {
	private String defaultRedirectURL = "redirect:/brands/page/1?sortField=name&sortDir=asc";
	@Autowired
	private BrandService service;

	@GetMapping("/brands")
	public String brands(Model model) {
		return null;
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@Param("pageNum") Integer pageNum, @PathVariable(name = "sortField") String name,
			@PathVariable(name = "sortDir") String sortDir) {
		
		//List<Brand> brands=service.listAll();
		return null;
	}

}
