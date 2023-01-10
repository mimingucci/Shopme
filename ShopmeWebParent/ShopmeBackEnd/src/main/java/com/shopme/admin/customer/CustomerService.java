package com.shopme.admin.customer;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class CustomerService {
    private static final int CUSTOMERS_PER_PAGE=10;
	@Autowired
	private CustomerRepository repo;
	
	@Autowired 
	private CountryRepository countryRepository;
	
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = repo.findByEmail(email);

		if (existCustomer != null && existCustomer.getId() != id) {
			return false;
		}
		
		return true;
	}

	public void listByPage(Integer pageNum, String sortField, String sortDir, String keyword, Model model) {
		Sort sort=sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
		Pageable pageable=PageRequest.of(pageNum-1, CUSTOMERS_PER_PAGE, sort);
		Page<Customer> listCustomersByPage = null;
		if(keyword.equals("") || keyword==null) {
			listCustomersByPage=repo.findAll(pageable);
		}else {
			listCustomersByPage=repo.findAll(keyword, pageable);
		}
		List<Customer> listItems = listCustomersByPage.getContent();
		int pageSize = listCustomersByPage.getSize();
		
		long startCount = (pageNum - 1) * pageSize + 1;
		long endCount = startCount + pageSize - 1;
		if (endCount > listCustomersByPage.getTotalElements()) {
			endCount = listCustomersByPage.getTotalElements();
		}
		if(sortDir.equals("asc")) {
			model.addAttribute("reverseSortDir}", "des");
		}else {
			model.addAttribute("reverseSortDir}", "asc");
		}
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("sortField", sortField);
		model.addAttribute("listCustomers", listItems);
		model.addAttribute("moduleURL", "/customers");
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", listCustomersByPage.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", listCustomersByPage.getTotalElements());
		//model.addAttribute(listName, listItems);
		model.addAttribute("listName", "customers");
		
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}

	public Customer get(Integer id) throws CustomerNotFoundException {
		Customer customer=repo.findById(id).get();
		if(customer==null) {
			throw new CustomerNotFoundException("Could not find customer with id: "+id);
		}else {
			return customer;
		}
	}
	
	
	
}
