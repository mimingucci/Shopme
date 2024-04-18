package com.shopme.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.setting.currency.CurrencyRepository;
import com.shopme.common.Constants;
import com.shopme.common.entity.Currency;
import com.shopme.common.entity.setting.Setting;

import antlr.StringUtils;

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
//			System.out.println(setting.getKey() + " -- " + setting.getValue());
		}

		model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
		return "settings/settings";
	}

	@PostMapping("/settings/save_general")
	public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
			HttpServletRequest request, RedirectAttributes ra) throws IOException {
		GeneralSettingBag settingBag = settingService.getGeneralSettings();

		saveSiteLogo(multipartFile, settingBag);
		saveCurrencySymbol(request, settingBag);

		updateSettingValuesFromForm(request, settingBag.list());

		ra.addFlashAttribute("message", "General settings have been saved.");

		return "redirect:/settings";
	}

	private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = org.springframework.util.StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String value = "/site-logo/" + fileName;
			settingBag.updateSiteLogo(value);

			String uploadDir = "site-logo";
//			FileUploadUtil.removeDir(uploadDir);
//			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		}
	}

	private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
		Integer currencyId = Integer.parseInt(request.getParameter("CURRENCY_ID"));
		Optional<Currency> findByIdResult = currencyRepository.findById(currencyId);

		if (findByIdResult.isPresent()) {
			Currency currency = findByIdResult.get();
			settingBag.updateCurrencySymbol(currency.getSymbol());
		}
	}

	private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
		for (Setting setting : listSettings) {
			String value = request.getParameter(setting.getKey());
			if (value != null) {
				setting.setValue(value);
			}
		}

		settingService.saveAll(listSettings);
	}

	@PostMapping("/settings/save_mail_server")
	public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> mailServers = settingService.getMailServerSettings();
		updateSettingValuesFromForm(request, mailServers);
		ra.addFlashAttribute("message", "Mail server settings have been saved");

		return "redirect:/settings#mailServer";
	}
	
	@PostMapping("/settings/save_mail_templates")
	public String saveMailTemplateSetttings(HttpServletRequest request, RedirectAttributes ra) {
		List<Setting> mailTemplateSettings = settingService.getMailTemplateSettings();
		updateSettingValuesFromForm(request, mailTemplateSettings);
		
		ra.addFlashAttribute("message", "Mail template settings have been saved");
		
		return "redirect:/settings#mailTemplates";
	}
}
