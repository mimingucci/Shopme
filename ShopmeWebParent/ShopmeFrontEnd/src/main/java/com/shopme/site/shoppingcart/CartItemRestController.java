package com.shopme.site.shoppingcart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

@RestController
public class CartItemRestController {
	@Autowired
	private CartItemService cartService;

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
}
