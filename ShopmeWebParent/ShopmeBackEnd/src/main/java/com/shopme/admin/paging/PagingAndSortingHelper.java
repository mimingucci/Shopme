package com.shopme.admin.paging;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class PagingAndSortingHelper {
	public static void updateModelAttributes(int pageNum, Page<?> page, String listName, Model model) {
		List<?> listItems = page.getContent();
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
		model.addAttribute("listName", listName);
	}
	public PagingAndSortingHelper() {
		// TODO Auto-generated constructor stub
	}
}
