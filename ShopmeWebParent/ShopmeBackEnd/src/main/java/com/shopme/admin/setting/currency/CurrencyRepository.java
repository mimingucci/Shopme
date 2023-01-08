package com.shopme.admin.setting.currency;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer>{
	public List<Currency> findAllByOrderByNameAsc();
}
