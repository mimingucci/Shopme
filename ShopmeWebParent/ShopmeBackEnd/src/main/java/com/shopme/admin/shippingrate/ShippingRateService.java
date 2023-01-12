package com.shopme.admin.shippingrate;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Service
@Transactional
public class ShippingRateService {

	@Autowired 
	private ShippingRateRepository repo;
	
	@Autowired
	private CountryRepository countryRepository;
	
	public void getShippingRate(String sortField, String sortDir, String keyword, int pageNum, Model model) {
		Pageable pageable=null;
		Sort sort=null;
		Page<ShippingRate> pageShippingRates=null;
		if(sortDir.equals("asc") || sortDir==null || sortDir.equals("")) {
			sort=Sort.by(sortField).ascending();
			pageable=PageRequest.of(pageNum-1, 10, sort);
		}else {
			sort=Sort.by(sortField).descending();
			pageable=PageRequest.of(pageNum-1, 10, sort);
		}
		if(keyword==null || keyword.equals("") || keyword.equals("null")) {
			 pageShippingRates=repo.findAll(pageable);
			 System.out.println("1"+pageShippingRates.getContent());
		}else {
			pageShippingRates=repo.findAll(keyword, pageable);
			System.out.println("2"+pageShippingRates.getContent());
		}
		String reverseSortDir = sortDir.equals("asc") ? "des" : "asc";
		PagingAndSortingHelper.updateModelAttributes(pageNum, pageShippingRates, "shipping_rates", model);
		model.addAttribute("shippingRates", pageShippingRates.getContent());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
	}

	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long shippingRate=repo.countById(id);
		if(shippingRate>0) {
			repo.deleteById(id);
		}else {
			throw new ShippingRateNotFoundException("Could not found shipping rate data with id: "+id);
		}
	}

	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
	}
	
	public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
		
		repo.updateCODSupport(id, codSupported);
	}

	public List<Country> listAllCountries() {
		return countryRepository.findAllByOrderByNameAsc();
	}

	public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
		ShippingRate rateInDB = repo.findByCountryAndState(
				rateInForm.getCountry().getId(), rateInForm.getState());
		boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
		boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);
		
		if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
			throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
						+ rateInForm.getCountry().getName() + ", " + rateInForm.getState()); 					
		}
		repo.save(rateInForm);
	}
}
