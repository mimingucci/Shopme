package com.shopme.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

//@DataJpaTest
//@AutoConfigureTestDatabase(replace = Replace.NONE)
//@Rollback(false)
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserRepositoryTests {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired 
	private TestEntityManager entityManager;
	
	@Test 
	public void getUserByEmail() {
		User user=userRepo.getUserByEmail("gtvvunguye@gmail.com");
	
		assertThat(user).isNotNull();
	}
}
