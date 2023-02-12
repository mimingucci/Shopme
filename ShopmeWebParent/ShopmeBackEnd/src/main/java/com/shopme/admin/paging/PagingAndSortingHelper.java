package com.shopme.admin.paging;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

import com.shopme.admin.setting.SettingRepository;
import com.shopme.common.Constants;

public class PagingAndSortingHelper {
	@Autowired
	private SettingRepository repo;
	
//	@Autowired
//	public PagingAndSortingHelper(SettingRepository repo) {
//		super();
//		this.repo = repo;
//	}
//	public String getSettingValueByKey(String key) {
//		String value=repo.findValueByKey(key);
//		return value;
//	}
	public static void updateModelAttributes(int pageNum, Page<?> page, String listName, Model model) {
		PagingAndSortingHelper helper=new PagingAndSortingHelper();
		List<?> listItems = page.getContent();
		int pageSize = page.getSize();
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
	  //  model.addAttribute("siteLogo", Constants.S3_BASE_URI+"/"+helper.getSettingValueByKey("SITE_LOGO"));
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
