package com.shopme.setting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.setting.SettingRepository;
import com.shopme.common.Constants;
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class testSettingModule {
	@Autowired
	public SettingRepository repo;

	@Test
	public void getUrlSiteLogo() {
		System.out.println(Constants.S3_BASE_URI);
	}
	
	@Test
	public void getValueByKey() {
		System.out.println(repo.findValueByKey("SITE_LOGO"));
	}
}
