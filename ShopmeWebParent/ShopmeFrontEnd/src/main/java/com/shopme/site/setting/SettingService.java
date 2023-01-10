package com.shopme.site.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	@Autowired private SettingRepository settingRepo;

	public List<Setting> getGeneralSettings() {
		return settingRepo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}
	
	public EmailSettingBag getEmailSettingBag() {
		List<Setting> emailSettingBags=settingRepo.findByCategory(SettingCategory.MAIL_SERVER);
		emailSettingBags.addAll(settingRepo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		return new EmailSettingBag(emailSettingBags);
	}
}
