package com.shopme.site.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerService;

@Controller
public class AddressController {

	
	private AddressService addressService;
	
	
	private CustomerService customerService;
	
	@Autowired
	public AddressController(AddressService addressService,@Lazy CustomerService customerService) {
		this.addressService=addressService;
		this.customerService=customerService;
	}
	
	@GetMapping("/address_book")
	public String showAddressBook(Model model, HttpServletRequest request) {
		String customerName=request.getUserPrincipal().getName();
		Customer customer = customerService.findCustomerByEmail(customerName);
		List<Address> listAddresses = addressService.listAddressBook(customer);
		
		boolean usePrimaryAddressAsDefault = true;
		if(listAddresses!=null || listAddresses.size()>0)
		for (Address address : listAddresses) {
			if (address.isDefaultForShipping()) {
				usePrimaryAddressAsDefault = false;
				break;
			}
		}
		model.addAttribute("redirectedFromCheckoutPage", false);
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("customer", customer.getFullName()+", "+customer.getAddressLine1()+", "+customer.getCity()+", "+customer.getCountry().getName()+". Postal Code: "+customer.getPostalCode()+". Phone Number: "+customer.getPhoneNumber());
		model.addAttribute("usePrimaryAddressAsDefault", usePrimaryAddressAsDefault);
		
		return "address_book/addresses";
	}
	
	@GetMapping("/address_book/new")
	public String newAddress(Model model) {
		List<Country> listCountries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("address", new Address());
		model.addAttribute("pageTitle", "Add New Address");
		
		return "address_book/address_form";
	}
	
	@PostMapping("/address_book/save")
	public String saveAddress(@ModelAttribute(name = "address") Address address, HttpServletRequest request, RedirectAttributes ra) {
		//Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		String emailCustomer=request.getUserPrincipal().getName();
		Customer customer=customerService.findCustomerByEmail(emailCustomer);
		address.setCustomer(customer);
		addressService.save(address);
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		if ("checkout".equals(redirectOption)) {
			redirectURL += "?redirect=checkout";
		}
		
		ra.addFlashAttribute("message", "The address has been saved successfully.");
		
		return redirectURL;
	}
	
	@GetMapping("/address_book/edit/{id}")
	public String editAddress(@PathVariable("id") Integer addressId, Model model,
			HttpServletRequest request) {
		String emailCustomer=request.getUserPrincipal().getName();
		Customer customer=customerService.findCustomerByEmail(emailCustomer);
		List<Country> listCountries = customerService.listAllCountries();
		
		Address address = addressService.get(addressId, customer.getId());

		model.addAttribute("address", address);
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");
		
		return "address_book/address_form";
	}
	
	@GetMapping("/address_book/delete/{id}")
	public String deleteAddress(@PathVariable("id") Integer addressId, RedirectAttributes ra,
			HttpServletRequest request) {
		String emailCustomer=request.getUserPrincipal().getName();
		Customer customer=customerService.findCustomerByEmail(emailCustomer);
		addressService.delete(addressId, customer.getId());
		
		ra.addFlashAttribute("message", "The address ID " + addressId + " has been deleted.");
		
		return "redirect:/address_book";
	}
	
	@GetMapping("/address_book/default/{id}")
	public String setDefaultAddress(@PathVariable("id") Integer addressId,
			HttpServletRequest request) {
		String emailCustomer=request.getUserPrincipal().getName();
		Customer customer=customerService.findCustomerByEmail(emailCustomer);
		addressService.setDefaultAddress(addressId, customer.getId());
		
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address_book";
		
		if ("cart".equals(redirectOption)) {
			redirectURL = "redirect:/cart";
		} else if ("checkout".equals(redirectOption)) {
			redirectURL = "redirect:/checkout";
		}
		
		return redirectURL; 
	}
}
