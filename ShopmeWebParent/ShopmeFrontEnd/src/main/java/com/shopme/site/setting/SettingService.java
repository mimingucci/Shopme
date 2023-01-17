package com.shopme.site.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;
import com.shopme.site.currency.CurrencyRepository;

@Service
public class SettingService {
	@Autowired private SettingRepository settingRepo;
	
	@Autowired
	private CurrencyRepository currencyRepo;

	public List<Setting> getGeneralSettings() {
		return settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
	public EmailSettingBag getEmailSettingBag() {
		List<Setting> emailSettingBags=settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
		emailSettingBags.addAll(settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(emailSettingBags);
	}
	
	public String getCurrencyCode() {
		Setting setting = settingRepo.findByKey("CURRENCY_ID");
		Integer currencyId = Integer.parseInt(setting.getValue());
		Currency currency = currencyRepo.findById(currencyId).get();
		
		return currency.getCode();
	}
}
