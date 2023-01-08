package com.shopme.admin.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.setting.currency.CurrencyRepository;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;

@Controller
public class SettingController {

	@Autowired
	private SettingService settingService;
	
	@Autowired
	private CurrencyRepository currencyRepository;
	
	@GetMapping("/settings")
	public String listAll(Model model) {
		List<Setting> listSettings = settingService.listAllSettings();
		List<Currency> listCurrencies = currencyRepository.findAllByOrderByNameAsc();
		
		model.addAttribute("listCurrencies", listCurrencies);
		
		for (Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		
		return "settings/settings";
	}
}
