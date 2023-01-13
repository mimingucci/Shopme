package com.shopme.admin.order;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderRestController {

	
}

class Response{
	private Integer orderId;
	private String status;

	public Response(Integer orderId, String status) {
		this.orderId = orderId;
		this.status = status;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}