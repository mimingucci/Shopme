package com.shopme.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PassworkEncoder {

	@Test
	public void encode() {
		BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
		String passwordencoder=encoder.encode("toivaban12345");
		System.out.println(passwordencoder);
	}
}
