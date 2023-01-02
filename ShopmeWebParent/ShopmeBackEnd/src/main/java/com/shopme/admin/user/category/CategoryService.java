package com.shopme.admin.user.category;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepo;
	
	
	public List<Category> listCategories() {
		return categoryRepo.findAll();
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		categoryRepo.updateEnabledStatus(id, enabled);
	}
	
	public List<Category> listCategoryByPage(int pageNum, String sortDir, String keyword){
		Sort sort=null;
		Pageable pageable=null;
		if(sortDir!=null || !sortDir.isEmpty()) {
			if(sortDir=="asc") {
				sort=Sort.by(keyword).ascending();
			}else {
				sort=Sort.by(keyword).descending();
			}
			pageable=(Pageable) PageRequest.of(pageNum-1, 5, sort);
		}else {
			pageable=(Pageable) PageRequest.of(pageNum-1, 5);
		}
		Page<Category> listCategoriesPage=categoryRepo.findSortedAndPagingCategories(pageable);
		List<Category> listCategoriesAfterSortedAndPaging=listCategoriesPage.getContent();
		return listCategoriesAfterSortedAndPaging;
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = categoryRepo.findByName(name);
		
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = categoryRepo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";	
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			
			Category categoryByAlias = categoryRepo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
			
		}
		
		return "OK";
	}
	
	public Category save(Category category) {
		Category parent = category.getParent();
		if (parent != null) {
			String allParentIds = parent.getAllParentIDs() == null ? "-" : parent.getAllParentIDs();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIDs(allParentIds);
		}
		
		return categoryRepo.save(category);
	}
	
}
