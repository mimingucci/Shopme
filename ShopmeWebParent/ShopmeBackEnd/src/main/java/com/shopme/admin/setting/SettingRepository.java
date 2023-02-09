package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String>{
	public List<Setting> findByCategory(SettingCategory category);
	@Query("SELECT s.value FROM Setting s WHERE s.key = ?1")
	public String findValueByKey(String key);
}
