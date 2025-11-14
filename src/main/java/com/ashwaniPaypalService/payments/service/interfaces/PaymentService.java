package com.ashwaniPaypalService.payments.service.interfaces;

import com.ashwaniPaypalService.payments.pojo.CreateOrderReq;
import com.ashwaniPaypalService.payments.pojo.OrderResponse;

public interface PaymentService {
	
	public OrderResponse createOrder(CreateOrderReq createOrderReq);

}
