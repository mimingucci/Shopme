package com.shopme.admin.user.category;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;

@Controller
public class CategoryController {
   @Autowired
   private CategoryService service;
   
   @GetMapping("/categories")
   public String listCategories(Model model) {
	   List<Category> categories=service.listCategories();
	   
	   model.addAttribute("listCategories", categories);
	   return "categories/categories";
   }
   
    @GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ? "enabled" : "disabled";
		String message = "The category ID " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		
		return "redirect:/categories";
	}
    
    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@Param("id") Integer id) {
    	System.out.println(id);
    	return "redirect:/categories";
    }
    
    @GetMapping("/categories/page/{pageNum}")
    public String sortAndPagingCategories(
    		@PathVariable("pageNum") int pageNum,
    		@RequestParam(name = "sortField", defaultValue = "name") String sortField,
    		                              @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
    		                              Model model) {
    	List<Category> listCategories=service.listCategoryByPage(pageNum, sortDir, sortField);
    	List<Category> categories=service.listCategories();
    	int totalPages;
 	   if(categories.size()%5!=0) {
 		   totalPages=categories.size()/5+1;
 	   }else {
 		   totalPages=categories.size()/5;
 	   }
 	   model.addAttribute("totalPages", totalPages);
 	   model.addAttribute("currentPage", pageNum);
 	   model.addAttribute("sortField", "name");
 	   model.addAttribute("sortDir", "asc");
       model.addAttribute("listCategories", listCategories);
    	return "categories/categories";
    }
    
    @GetMapping("/categories/new")
    public String createNewCategories(Model model) {
        List<Category> listCategories = service.listCategories();
		
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		
		return "categories/category_form";
    }
    
    @PostMapping("/categories/save")
	public String saveCategory(Category category, 
			@RequestParam("fileImage") MultipartFile multipartFile,
			RedirectAttributes ra) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			category.setImage(fileName);

			Category savedCategory = service.save(category);
			String uploadDir = "/category-images/" + savedCategory.getId();
			
		FileUploadUtil.removeDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			service.save(category);
		}
		
		ra.addFlashAttribute("message", "The category has been saved successfully.");
		return "redirect:/categories";
	}
}
