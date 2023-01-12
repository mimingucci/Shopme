package com.shopme.site.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.site.setting.CountryRepository;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private CountryRepository countryRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	public List<Country> listAllCountries(){
		return countryRepository.findAllByOrderByNameAsc();
	}
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepository.findByEmail(email);
		return customer == null;
	}

	public void registerCustomer(Customer customer) {
		String encodedPassword=encoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
		customer.setEnabled(false);
		customer.setAuthenticationType(AuthenticationType.DATABASE);
		customer.setCreatedTime(new Date());
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		customerRepository.save(customer);
		
	}
	
	public boolean verify(String verificationCode) {
		Customer customer=customerRepository.findByVerificationCode(verificationCode);
		if(customer==null || customer.isEnabled()) {
			return false;
		}else {
			customerRepository.enable(customer.getId());
			return true;
		}
	}
	
	public void updateAuthenticationType(Customer customer, AuthenticationType auth) {
		if(!customer.getAuthenticationType().equals(auth)) {
			customerRepository.updateAuthenticationType(customer.getId(), auth);
		}
	}
	
	public Customer findCustomerByEmail(String email) {
		return customerRepository.findByEmail(email);
	}
	
	public void addNewCustomerUponOAuthLogin(String name, String email, String countryCode,
			AuthenticationType authenticationType) {
		Customer customer = new Customer();
		customer.setEmail(email);
		setName(name, customer);
		
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setPassword("");
		customer.setAddressLine1("");
		customer.setCity("");
		customer.setState("");
		customer.setPhoneNumber("");
		customer.setPostalCode("");
		customer.setCountry(countryRepository.findByCode(countryCode));
		
		customerRepository.save(customer);
	}	
	
	private void setName(String name, Customer customer) {
		String[] nameArray = name.split(" ");
		if (nameArray.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("");
		} else {
			String firstName = nameArray[0];
			customer.setFirstName(firstName);
			
			String lastName = name.replaceFirst(firstName + " ", "");
			customer.setLastName(lastName);
		}
	}
	
	
}
