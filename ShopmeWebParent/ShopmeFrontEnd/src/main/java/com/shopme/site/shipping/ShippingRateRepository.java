package com.shopme.site.shipping;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends CrudRepository<ShippingRate, Integer>{

	public ShippingRate findByCountryAndState(Country country, String state);
	
	@Query(value = "SELECT * FROM shipping_rates WHERE country_id = :country_id AND state = :state", nativeQuery = true )
	public ShippingRate findShippingRateByCountryAndState(@Param("country_id") Integer country_id,@Param("state") String state);
}
