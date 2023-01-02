package com.shopme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class ShopmeBackEndApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void endcode() {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		String passwordEncoder=encoder.encode("toivaban12345");
		System.out.println(passwordEncoder);
	}
}
