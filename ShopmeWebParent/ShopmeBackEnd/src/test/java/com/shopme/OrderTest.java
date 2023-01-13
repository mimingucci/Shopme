package com.shopme;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

import com.shopme.admin.ShopmeBackEndApplication;
import com.shopme.admin.order.OrderDetailRepository;
import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.Product;
@ContextConfiguration(classes = ShopmeBackEndApplication.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderTest {
	@Autowired private OrderRepository orderrepo;
	@Autowired private OrderDetailRepository repo;
	@Autowired private TestEntityManager entityManager;
	@Test
	public void test() {
		Product product = entityManager.find(Product.class, 1);
		OrderDetail orderDetail=new OrderDetail();
		orderDetail.setProduct(product);
		orderDetail.setQuantity(2);
		orderDetail.setShippingCost(3);
		orderDetail.setUnitPrice((float)2.3);
		OrderDetail savedOrderDetail= repo.save(orderDetail);
		assertThat(savedOrderDetail.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewOrderWithSingleProduct() {
		Customer customer = entityManager.find(Customer.class, 1);
		Product product = entityManager.find(Product.class, 1);
		
		Order mainOrder = new Order();
		mainOrder.setOrderTime(new Date());
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();
		
		mainOrder.setShippingCost(10);
		mainOrder.setProductCost(product.getCost());
		mainOrder.setTax(0);
		mainOrder.setSubtotal(product.getPrice());
		mainOrder.setTotal(product.getPrice() + 10);
		
		mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		mainOrder.setStatus(OrderStatus.NEW);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(1);
		
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setProduct(product);
		orderDetail.setOrder(mainOrder);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setShippingCost(10);
		orderDetail.setQuantity(1);
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setUnitPrice(product.getPrice());
		
		mainOrder.getOrderDetails().add(orderDetail);
		
		Order savedOrder = orderrepo.save(mainOrder);
		
		assertThat(savedOrder.getId()).isGreaterThan(0);		
	}
	@Test
	public void testListOrders() {
		Iterable<Order> orders = orderrepo.findAll();
		
		assertThat(orders).hasSizeGreaterThan(0);
		
		orders.forEach(System.out::println);
	}
	
	@Test
	public void testUpdateOrder() {
		Integer orderId = 1;
		Order order = orderrepo.findById(orderId).get();
		
		order.setStatus(OrderStatus.SHIPPING);
		order.setPaymentMethod(PaymentMethod.COD);
		order.setOrderTime(new Date());
		order.setDeliverDays(2);
		
		Order updatedOrder = orderrepo.save(order);
		
		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}
}
