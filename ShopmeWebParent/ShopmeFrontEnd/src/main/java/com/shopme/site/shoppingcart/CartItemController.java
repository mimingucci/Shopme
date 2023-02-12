package com.shopme.site.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.site.Utility;
import com.shopme.site.address.AddressService;

@Controller
public class CartItemController {
	@Autowired
	private CartItemService cartService;
	
	@Autowired
	private AddressService addressService;
	
	public Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		Object customer =  request.getUserPrincipal();
		if(customer==null) {
			throw new CustomerNotFoundException("Not found customer");
		}
		
		String emailCustomer=null;
		if(customer instanceof UsernamePasswordAuthenticationToken || customer instanceof RememberMeAuthenticationToken) {
			emailCustomer=request.getUserPrincipal().getName();
		}
		return cartService.getCustomerByEmail(emailCustomer);
		
	}
}
