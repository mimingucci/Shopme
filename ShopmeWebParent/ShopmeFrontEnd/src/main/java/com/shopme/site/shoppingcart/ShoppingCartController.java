package com.shopme.site.shoppingcart;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.ShippingRate;
import com.shopme.site.Utility;
import com.shopme.site.address.AddressService;
import com.shopme.site.customer.CustomerRepository;
import com.shopme.site.oauth2.CustomerOauth2User;
import com.shopme.site.shipping.ShippingRateService;

@Controller
public class ShoppingCartController {

	@Autowired private CustomerRepository customerRepository;
	@Autowired private ShoppingCartService cartService;
	@Autowired private AddressService addressService;
	@Autowired private ShippingRateService shipService;
	
	@GetMapping("/cart")
	public String viewCart(Model model, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		if(customer==null) {
			return "error/404";
		}
		List<CartItem> cartItems = cartService.listCartItems(customer);
		
		float estimatedTotal = 0.0F;
		
		for (CartItem item : cartItems) {
			estimatedTotal += item.getSubtotal();
		}
		
		Address defaultAddress = addressService.getDefaultAddress(customer);
		ShippingRate shippingRate = null;
		boolean usePrimaryAddressAsDefault = false;
		
		if (defaultAddress != null) {
			shippingRate = shipService.getShippingRateForAddress(defaultAddress);
		} else {
			usePrimaryAddressAsDefault = true;
			shippingRate = shipService.getShippingRateForCustomer(customer);
		}
		
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		model.addAttribute("shippingSupported", shippingRate != null);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("estimatedTotal", estimatedTotal);
		
		return "cart/shopping_cart";
	}
	
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email=null;
		Object principal = request.getUserPrincipal();
		//System.out.println("principal: "+principal.get);
		if (principal == null) return null;
		
		String customerEmail = null;
		
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			customerEmail = request.getUserPrincipal().getName();
		
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOauth2User oauth2User = (CustomerOauth2User) oauth2Token.getPrincipal();
			customerEmail = oauth2User.getEmail();
		}
		Customer customer=customerRepository.findByEmail(customerEmail);
		return customer;
	}
}
