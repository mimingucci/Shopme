package com.shopme.admin.customer;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CustomerService {
    private static final int CUSTOMERS_PER_PAGE=10;
	@Autowired
	private CustomerRepository repo;
	
	
	
}
