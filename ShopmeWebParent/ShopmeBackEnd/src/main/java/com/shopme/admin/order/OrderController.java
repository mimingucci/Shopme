package com.shopme.admin.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.setting.Setting;

@Controller
public class OrderController {

	public static final String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=default";
	
	@Autowired private OrderService orderService;
	@Autowired private SettingService settingService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(
			@RequestParam(name = "sortField", defaultValue = "orderTime", required = false) String sortField,
			@RequestParam(name="sortDir", defaultValue = "default", required = false) String sortDir,
			@RequestParam(name="keyword", required = false) String keyword,
			@PathVariable(name = "pageNum") int pageNum,
			HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser,
			Model model
			) {

		orderService.listByPage(pageNum, sortField, sortDir, keyword, model);
		loadCurrencySetting(request);
		
		if (!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}

		model.addAttribute("sortDir", sortDir);
		
		return "orders/orders";
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model,
								   RedirectAttributes ra, HttpServletRequest request,
								   @AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);

			boolean isVisibleForAdminOrSalesperson = false;

			if (loggedUser.hasRole("Admin") || loggedUser.hasRole("Salesperson")) {
				isVisibleForAdminOrSalesperson = true;
			}

			model.addAttribute("isVisibleForAdminOrSalesperson", isVisibleForAdminOrSalesperson);
			model.addAttribute("order", order);

			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}

	}
	
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}	
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id,  RedirectAttributes ra) {
		try {
			orderService.delete(id);;
			ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return defaultRedirectURL;
	}
	
	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		
		//updateProductDetails(order, request);

		orderService.save(order);		
		
		ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");
		
		return defaultRedirectURL;
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			
			List<Country> listCountries = orderService.listAllCountries();
			
			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);
			
			return "orders/order_form";
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
		
	}
}
