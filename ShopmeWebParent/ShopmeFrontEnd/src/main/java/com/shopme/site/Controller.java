package com.shopme.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Category;
import com.shopme.site.category.CategoryService;

@org.springframework.stereotype.Controller
public class Controller {
	@Autowired
	private CategoryService categoryService;

	@GetMapping("")
	public String viewHomePage(Model model) {
		List<Category> listCategories = categoryService.listNoChildrenCategories();
		model.addAttribute("listCategories", listCategories);

		return "index";
	}
}
