package com.shopme.admin.product;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductService {
	public static final int PRODUCTS_PER_PAGE = 5;
	@Autowired
	private ProductRepository repo;

	public List<Product> listAll() {
		return (List<Product>) repo.findAll();
	}

//	public void listByPage(int pageNum, String sortField, String sortDir, String keyword,Integer categoryId, Model model) {
//		Sort sort = Sort.by(sortField);
//		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
//		Pageable pageable=PageRequest.of(pageNum-1, PRODUCTS_PER_PAGE, sort);
//		Page<Product> page = null;
//		
//		if (keyword != null && !keyword.isEmpty()) {
//			if (categoryId != null && categoryId > 0) {
//				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
//				page = repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
//			} else {
//				page = repo.findAll(keyword, pageable);
//			}
//		} else {
//			if (categoryId != null && categoryId > 0) {
//				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
//				page = repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
//			} else {		
//				page = repo.findAll(pageable);
//			}
//		}
//		
//		List<Product> listItems = page.getContent();
//		int pageSize = page.getSize();
//		
//		long startCount = (pageNum - 1) * pageSize + 1;
//		long endCount = startCount + pageSize - 1;
//		if (endCount > page.getTotalElements()) {
//			endCount = page.getTotalElements();
//		}
//		
//		model.addAttribute("currentPage", pageNum);
//		model.addAttribute("totalPages", page.getTotalPages());
//		model.addAttribute("startCount", startCount);
//		model.addAttribute("endCount", endCount);
//		model.addAttribute("totalItems", page.getTotalElements());
//		model.addAttribute("listName", listItems);
//	}

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
		// repo.updateReviewCountAndAverageRating(updatedProduct.getId());

		return updatedProduct;
	}

	public void listByPage(String sortField, String sortDir, String keyword, Integer pageNum, Integer categoryId,
			Model model) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
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
		String reverseSortDir = sortDir.equals("asc") ? "des" : "asc";
		PagingAndSortingHelper.updateModelAttributes(pageNum, page, "products", model);
		model.addAttribute("listProducts", page.getContent());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
	}

	public Product get(Integer id) throws Exception {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new Exception("Could not find any product with ID " + id);
		}
	}

	public void updateProductEnabledStatus(Integer id, boolean enabled) {

		repo.updateEnabledStatus(id, enabled);
	}

	public void delete(Integer id) throws Exception {
		Long countById = repo.countById(id);

		if (countById == null || countById == 0) {
			throw new Exception("Could not find any product with ID " + id);
		}
		repo.deleteById(id);

	}

	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);

		if (isCreatingNew) {
			if (productByName != null)
				return "Duplicate";
		} else {
			if (productByName != null && productByName.getId() != id) {
				return "Duplicate";
			}
		}

		return "OK";
	}

}
