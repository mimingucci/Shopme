package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;


@Service
@Transactional
public class UserService {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listAll() {
		return (List<User>) userRepo.findAll(Sort.by("firstName").ascending());
	}
	
//	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
//		helper.listEntities(pageNum, USERS_PER_PAGE, userRepo);
//	}
	public User getByEmail(String email) {
		User user=userRepo.getUserByEmail(email);
		return user;
	}
	
	public Page<User> listByPage(int pageNum){
		int pageSize=5;
		Pageable pageable=PageRequest.of(pageNum-1, pageSize);
		return userRepo.findAll(pageable);
	}

	public Page<User> listByPageWithKeyword(int pageNum, String keyword){
		int pageSize=5;
		Pageable pageable=PageRequest.of(pageNum-1, pageSize);
		return userRepo.findAll(keyword, pageable);
	}
	
	public Page<User> listBySortedPage(int pageNum, Sort sort){
		int pageSize=5;
		Pageable pageable=PageRequest.of(pageNum-1, pageSize, sort);
		return userRepo.findAll(pageable);
	}

	public Page<User> listBySortedPageWithKeyword(int pageNum, Sort sort, String keyword){
		int pageSize=5;
		Pageable pageable=PageRequest.of(pageNum-1, pageSize, sort);
		return userRepo.findAll(keyword, pageable);
	}
	
	public List<Role> listRoles() {
		return (List<Role>) roleRepo.findAll();
	}
	
	public User save(User user) {
		boolean isUpdatingUser = (user.getId() != null);
		
		if (isUpdatingUser) {
			User existingUser = userRepo.findById(user.getId()).get();
			
			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
			
		} else {		
			encodePassword(user);
		}
		
		return userRepo.save(user);
	}
	
	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		
		userRepo.deleteById(id);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);
		
		if (userByEmail == null) return true;
		
		boolean isCreatingNew = (id == null);
		
		if (isCreatingNew) {
			if (userByEmail != null) return false;
		} else {
			if (userByEmail.getId() != id) {
				return false;
			}
		}
		
		return true;
	}
	
	private void encodePassword(User user) {
		String encodedPassword=passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		userRepo.updateEnabledStatus(id, enabled);
	}

	public User updateAccount(User user) {
        User userInDB = userRepo.findById(user.getId()).get();
		
		if (!user.getPassword().isEmpty()) {
			userInDB.setPassword(user.getPassword());
			encodePassword(userInDB);
		}
		
		if (user.getPhotos() != null) {
			userInDB.setPhotos(user.getPhotos());
		}
		
		userInDB.setFirstName(user.getFirstName());
		userInDB.setLastName(user.getLastName());
		
		return userRepo.save(userInDB);
	}
}
