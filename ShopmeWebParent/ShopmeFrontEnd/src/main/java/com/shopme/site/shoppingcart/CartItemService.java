package com.shopme.site.shoppingcart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.site.customer.CustomerService;
import com.shopme.site.product.ProductRepository;

@Service
@Transactional
public class CartItemService {

	@Autowired
	private CartItemRepository repo;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ProductRepository productRepo;

	public Integer addProduct(Integer productId, Integer quantity, Customer customer) 
			throws ShoppingCartException {
		Integer updatedQuantity = quantity;
		Product product = new Product(productId);
		
		CartItem cartItem = repo.findByCustomerAndProduct(customer, product);
		
		if (cartItem != null) {
			updatedQuantity = cartItem.getQuantity() + quantity;
			
			if (updatedQuantity > 5) {
				throw new ShoppingCartException("Could not add more " + quantity + " item(s)"
						+ " because there's already " + cartItem.getQuantity() + " item(s) "
						+ "in your shopping cart. Maximum allowed quantity is 5.");
			}
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
		}
		
		cartItem.setQuantity(updatedQuantity);
		
		repo.save(cartItem);
		
		return updatedQuantity;
	}
	
	public Customer getCustomerByEmail(String email) {
		return customerService.findCustomerByEmail(email);
	}

	public List<CartItem> listCartItems(Customer customer) {
		return repo.findByCustomer(customer);
	}

	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		repo.updateQuantity(quantity, customer.getId(), productId);
		Product product = productRepo.findById(productId).get();
		float subtotal = product.getDiscountPrice() * quantity;
		return subtotal;
	}
	
	public void removeCartByCustomer(Integer id, Customer customer) {
		repo.deleteByCustomerAndProduct(customer.getId(), id);
	}
	
	
}
