package com.shopme.admin.shippingrate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Controller
public class ShippingRateController {
private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=country&sortDir=asc";
	
	@Autowired private ShippingRateService service;
	
	@Autowired
	private CountryRepository repo;

	@GetMapping("/shipping_rates")
	public String shippingrate() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/page/{pageNum}")
	public String shippingRateByPage(@PathVariable(name = "pageNum", required = true) int pageNum, 
			                         @RequestParam(name = "sortField", defaultValue = "country_id") String sortField, 
			                         @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
			                         @RequestParam(name="keyword", required = false) String keyword, Model model) {
		
		service.getShippingRate(sortField, sortDir, keyword, pageNum, model);
		return "shippingrates/shipping_rates";
	}
	
	@GetMapping("/shipping_rates/delete/{id}")
	public String deleteRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			service.delete(id);
			ra.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
	
	@GetMapping("/shipping_rates/edit/{id}")
	public String editRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			ShippingRate rate = service.get(id);
			List<Country> listCountries = service.listAllCountries();
			
			model.addAttribute("listCountries", listCountries);			
			model.addAttribute("rate", rate);
			model.addAttribute("pageTitle", "Edit Rate (ID: " + id + ")");
			
			return "shippingrates/shipping_rate_form";
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}
	
	@GetMapping("/shipping_rates/new")
	public String newRate(Model model) {
		List<Country> listCountries=service.listAllCountries();
		model.addAttribute("rate", new ShippingRate());
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Create New Rate");
		return "shippingrates/shipping_rate_form";
	}
	
	@GetMapping("/shipping_rates/cod/{id}/enabled/{supported}")
	public String updateCODSupport(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "supported") Boolean supported,
			Model model, RedirectAttributes ra) {
		try {
			service.updateCODSupport(id, supported);
			ra.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
	
	@PostMapping("/shipping_rates/save")
	public String saveRate(ShippingRate rate, RedirectAttributes ra) {
		try {
			service.save(rate);
			ra.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (ShippingRateAlreadyExistsException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return defaultRedirectURL;
	}
}
