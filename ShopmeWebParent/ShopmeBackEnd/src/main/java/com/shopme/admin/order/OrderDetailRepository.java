package com.shopme.admin.order;

import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.OrderDetail;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer>{

}
