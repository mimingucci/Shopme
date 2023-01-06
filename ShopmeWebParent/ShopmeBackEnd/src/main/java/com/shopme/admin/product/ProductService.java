package com.shopme.admin.product;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 5;
    @Autowired private ProductRepository repo;
	
	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}
	
	public void listByPage(int pageNum, String sortField, String sortDir, String keyword,Integer categoryId, Model model) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable=PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE, sort);
		Page<Product> page = null;
		
		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			} else {
				page = repo.findAll(keyword, pageable);
			}
		} else {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				page = repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
			} else {		
				page = repo.findAll(pageable);
			}
		}
		
		List<Product> listItems = page.getContent();
		int pageSize = page.getSize();
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listName", listItems);
	}

	public void saveProductPrice(Product product) {
		Product productInDB = repo.findById(product.getId()).get();
		productInDB.setCost(product.getCost());
		productInDB.setPrice(product.getPrice());
		productInDB.setDiscountPercent(product.getDiscountPercent());
		
		repo.save(productInDB);
	}

	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}
		
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		Product updatedProduct = repo.save(product);
		//repo.updateReviewCountAndAverageRating(updatedProduct.getId());
		
		return updatedProduct;
	}	
	
}
