package com.shopme.site.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	@Autowired
	private DatabaseLoginSuccessHandler loginSuccessHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CustomerUserDetailsService customerUserDetailsService() {
		return new CustomerUserDetailsService();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.authorizeRequests()
				.antMatchers("/account_details", "/update_account_details", "/orders/**", "/cart", "/address_book/**",
						"/checkout", "/place_order", "/reviews/**", "/process_paypal_order", "/write_review/**",
						"/post_review")
				.authenticated()
				.anyRequest()
				.permitAll()
				.and()
				      .formLogin()
				      .loginPage("/login")
				      .defaultSuccessUrl("/")
				      .usernameParameter("email")
				      .successHandler(loginSuccessHandler).permitAll()
				.and()
				   .logout().permitAll()
				.and()
				   .rememberMe()
				   .key("toivaban12345")
				   .tokenValiditySeconds(14 * 60 * 60 * 24)
				      ;
                
		http.headers().frameOptions().sameOrigin();
		return http.build();
	}
	
	@Bean
	   public AuthenticationManager authenticationManager(
	           AuthenticationConfiguration authConfig) throws Exception {
	       return authConfig.getAuthenticationManager();
	   }
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customerUserDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");

	}

}
