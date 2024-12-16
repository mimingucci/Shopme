package com.shopme.admin.user.category;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	@Query("SELECT c FROM Category c")
	public Page<Category> findSortedAndPagingCategories(Pageable pageable);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);	
	
    public Long countById(Integer id);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);

	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);

	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public List<Category> totalElements(String keyword);

	@Query("SELECT c FROM Category c WHERE c.parent.id is NULL")
	public List<Category> findRootCategories(Sort sort);
}
