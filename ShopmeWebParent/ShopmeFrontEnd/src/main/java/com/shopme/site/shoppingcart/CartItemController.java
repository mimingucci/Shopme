package com.shopme.site.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.site.Utility;

@Controller
public class CartItemController {
	@Autowired
	private CartItemService cartService;
	
//	@GetMapping("/cart")
//	public String viewCart(Model model, HttpServletRequest request) {
//		Customer customer=null;
//		try {
//			customer = getAuthenticatedCustomer(request);
//		} catch (CustomerNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<CartItem> cartItems = cartService.listCartItems(customer);
//		
//		float estimatedTotal = 0.0F;
//		
//		for (CartItem item : cartItems) {
//			estimatedTotal += item.getSubtotal();
//		}
//		
//		//Address defaultAddress = addressService.getDefaultAddress(customer);
//		//ShippingRate shippingRate = null;
//		boolean usePrimaryAddressAsDefault = false;
//		
////		if (defaultAddress != null) {
////			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
////		} else {
////			usePrimaryAddressAsDefault = true;
////			shippingRate = shipService.getShippingRateForCustomer(customer);
////		}
//		
//		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
//		//model.addAttribute("shippingSupported", shippingRate != null);
//		model.addAttribute("cartItems", cartItems);
//		model.addAttribute("estimatedTotal", estimatedTotal);
//		
//		return "cart/shopping_cart";
//	}
//	
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
