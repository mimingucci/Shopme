package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
@Transactional
public class BrandService {
    public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandRepository repo;
	
	public List<Brand> listAll() {
		return (List<Brand>) repo.findAll();
	}
	
	
//	public List<Brand> listAll(Pageable pageable) {
//		return (List<Brand>) repo.findAll(pageable);
//	}
	
//	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
//		helper.listEntities(pageNum, BRANDS_PER_PAGE, repo);
//	}
	
	public List<Brand> allBrands(Pageable pageable){
		Page<Brand> pageBrand=repo.findAll(pageable);
		return pageBrand.getContent();
	}
	
	public List<Brand> allSearchBrands(String keyword, Pageable pageable){
		Page<Brand> pageBrand=repo.findAll(keyword, pageable);
		return pageBrand.getContent();
	}
	
	public Brand save(Brand brand) {
		return repo.save(brand);
	}
	
	public Brand get(Integer id) throws Exception {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new Exception("Could not find any brand with ID " + id);
		}
	}
	
	public void delete(Integer id) throws Exception {
		Long countById = repo.countById(id);
		
		if (countById == null || countById == 0) {
			throw new Exception("Could not find any brand with ID " + id);			
		}
		
		repo.deleteById(id);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Brand brandByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (brandByName != null) return "Duplicate";
		} else {
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	
}
