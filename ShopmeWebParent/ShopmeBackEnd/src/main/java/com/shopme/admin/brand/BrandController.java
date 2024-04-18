package com.shopme.admin.brand;

import java.io.IOException;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.shopme.admin.user.category.CategoryService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Controller
public class BrandController {
	private Integer BRANDS_PER_PAGE = 10;
	private String defaultRedirectURL = "redirect:/brands/page/1?sortField=id&sortDir=asc";
	@Autowired
	private BrandService service;

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/brands")
	public String brands(Model model) {
//		Sort sort= Sort.by("id").ascending();
//		Pageable pageable=PageRequest.of(0, 10, sort);
//		List<Brand> listBrands=service.allBrands(pageable);
//		model.addAttribute("listBrands", listBrands);
		return defaultRedirectURL;
	}

	@GetMapping("/brands/new")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryService.listCategories();

		model.addAttribute("listCategories", listCategories);
		model.addAttribute("brand", new Brand());
		model.addAttribute("pageTitle", "Create New Brand");

		return "brands/brand_form";
	}

	@GetMapping("/brands/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") Integer pageNum, 
			@PathParam("sortField") String sortField,
			@PathParam("sortDir") String sortDir, 
			@RequestParam(name = "keyword", defaultValue = "") String keyword,
			@RequestParam(name = "previousPage", defaultValue = "1") Integer previousPage,
			Model model) {
		System.out.println("current page"+pageNum);
		Pageable pageable = null;
		Sort sort = null;
		List<Brand> listBrands = null;
		List<Brand> allBrands = null;
		if (sortField == null || sortField.equals("null") || sortField.length() == 0) {
			sortField = "id";
		}
		if (sortDir == null || sortDir.equals("null") || sortDir.length() == 0) {
			sortDir = "asc";
		}
		if (pageNum == null || pageNum < 1) {
			pageNum = 1;
		}
		if (sortDir == "asc") {
			pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, Sort.by(sortField).ascending());
		} else {
			pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, Sort.by(sortField).descending());
		}
		Integer startCount = (pageNum - 1) * BRANDS_PER_PAGE + 1;
		Integer endCount = startCount + BRANDS_PER_PAGE - 1;
		if (keyword == null || keyword.isEmpty() || keyword.length()==0) {
			listBrands = service.allBrands(pageable);
			allBrands = service.listAll();
			if (allBrands.size() % 10 != 0) {
				int totalPage = (Integer) allBrands.size() / 10 + 1;
				
				model.addAttribute("totalPages", totalPage);
			} else {
				int totalPage = allBrands.size() / 10;
				
				model.addAttribute("totalPages", totalPage);
			}
			
			model.addAttribute("totalItems", allBrands.size());
			model.addAttribute("listName", "brands");
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			return "brands/brands";
		} else {

			listBrands = service.allSearchBrands(keyword, pageable);
			if (listBrands.size() % 10 != 0) {
				int totalPage = listBrands.size() / 10 + 1;
				model.addAttribute("totalPages", totalPage);
			} else {
				int totalPage = listBrands.size() / 10;
				model.addAttribute("totalPages", totalPage);
			}
			if (listBrands.size() < 1) {
				model.addAttribute("message", "Don't find any brand with keyword : '" + keyword + "'");
				pageable = PageRequest.of(previousPage-1, BRANDS_PER_PAGE, Sort.by("id").ascending());
				listBrands = service.allBrands(pageable);
				allBrands = service.listAll();
				if (allBrands.size() % 10 != 0) {
					int totalPage = (Integer) allBrands.size() / 10 + 1;
					
					model.addAttribute("totalPages", totalPage);
				} else {
					int totalPage = allBrands.size() / 10;
					
					model.addAttribute("totalPages", totalPage);
				}
				
				model.addAttribute("totalItems", allBrands.size());
				model.addAttribute("listName", "brands");
				model.addAttribute("listBrands", listBrands);
				model.addAttribute("currentPage", pageNum);
				model.addAttribute("startCount", previousPage);
				model.addAttribute("endCount", endCount);
				return "brands/brands";
			}

			if (endCount > listBrands.size()) {
				endCount = listBrands.size();
			}
			model.addAttribute("currentPage", pageNum);

			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("listName", "brands");
			model.addAttribute("listBrands", listBrands);
			model.addAttribute("totalItems", listBrands.size());

			return "brands/brands";
		}

//		Integer startCount = (pageNum - 1) * BRANDS_PER_PAGE + 1;
//		Integer endCount = startCount + BRANDS_PER_PAGE - 1;
//		if (endCount > listBrands.size()) {
//			endCount = listBrands.size();
//		}
//		model.addAttribute("currentPage", pageNum);
//		
//		model.addAttribute("startCount", startCount);
//		model.addAttribute("endCount", endCount);
//		model.addAttribute("listName", "brands");
//		model.addAttribute("listBrands", listBrands);
//		if(keyword==null || keyword.isEmpty()) {
//			model.addAttribute("totalItems", allBrands.size());
//		}else {
//			model.addAttribute("totalItems", listBrands.size());
//		}
		// return "brands/brands";
	}

	@PostMapping("/brands/save")
	public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes ra)
			throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			brand.setLogo(fileName);

			Brand savedBrand = service.save(brand);
			String uploadDir = "brand-logos/" + savedBrand.getId();

//			FileUploadUtil.removeDir(uploadDir);
//			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		} else {
			service.save(brand);
		}

		ra.addFlashAttribute("message", "The brand has been saved successfully.");
		return defaultRedirectURL;
	}

	@GetMapping("/brands/edit/{id}")
	public String editBrand(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Brand brand = service.get(id);
			List<Category> listCategories = categoryService.listCategories();

			model.addAttribute("brand", brand);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");

			return "brands/brand_form";
		} catch (Exception ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/brands/delete/{id}")
	public String removeBrand(@PathVariable("id") Integer id, RedirectAttributes ra) {
		try {
			String dirFolder = "brand-logos/" + id;
			service.delete(id);
			AmazonS3Util.removeFolder(dirFolder);
//			FileUploadUtil.removeDir(dirFolder);
			ra.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully");
		} catch (Exception e) {

			e.printStackTrace();
			ra.addFlashAttribute("message", e.getMessage());
		}
		return defaultRedirectURL;
	}

}
