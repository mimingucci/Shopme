package com.shopme.site.oauth2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerService;
@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	@Autowired
	private CustomerService service;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		CustomerOauth2User oauth2User=(CustomerOauth2User) authentication.getPrincipal();
		String name=oauth2User.getName();
		String email=oauth2User.getEmail();
		String countryCode=request.getLocale().getCountry();
		String clientName=oauth2User.getClientName();
		AuthenticationType authenticationType=getAuthenticationType(clientName);
		Customer customer=service.findCustomerByEmail(email);
		if(customer==null) {
			service.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
		}else {
			oauth2User.setFullName(customer.getFullName());
			service.updateAuthenticationType(customer, authenticationType);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
	private AuthenticationType getAuthenticationType(String clientName) {
		if (clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		} else {
			if(clientName.equals("Facebook")) {
				return AuthenticationType.FACEBOOK;
			}else {
				return AuthenticationType.DATABASE;
			}
			
		}
	}
}
