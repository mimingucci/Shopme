package com.shopme.site.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.site.customer.CustomerService;

@RestController
public class CartItemRestController {
	@Autowired
	private CartItemService cartService;
	
	@Autowired
	private CustomerService customerService;

	@PostMapping("/cart/add/{productId}/{quantity}")
	public String addProductToCart(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		
		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = cartService.addProduct(productId, quantity, customer);
			
			return updatedQuantity + " item(s) of this product were added to your shopping cart.";
		} catch (CustomerNotFoundException ex) {
			return "You must login to add this product to cart.";
		} catch (ShoppingCartException ex) {
			return ex.getMessage();
		}
		
	}

	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
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
	
	@PostMapping("/cart/update/{productId}/{quantity}")
	public String updateQuantity(@PathVariable("productId") Integer productId,
			@PathVariable("quantity") Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subtotal = cartService.updateQuantity(productId, quantity, customer);
			
			return String.valueOf(subtotal);
		} catch (CustomerNotFoundException ex) {
			return "You must login to change quantity of product.";
		}	
	}
	
	@DeleteMapping("/cart/remove/{productId}")
	public String removeCustomer(@PathVariable(name = "productId") Integer id, HttpServletRequest request) {
		try {
			Customer customer=getAuthenticatedCustomer(request);
			cartService.removeCartByCustomer(id, customer);
			return "The product has been removed from your shopping cart.";
		} catch (CustomerNotFoundException e) {
			// TODO: handle exception
			return "You must login to remove product.";
		}
	}
}
