package com.ashwaniPaypalService.payments.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ashwaniPaypalService.payments.pojo.CreateOrderReq;
import com.ashwaniPaypalService.payments.pojo.OrderResponse;
import com.ashwaniPaypalService.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/orders")
public class PaymentController {
	
	private final PaymentService paymentService;
	
	@PostMapping
	public OrderResponse createOrder(@RequestBody CreateOrderReq createOrderReq) {
		log.info("Creating order in PayPal provider service"
				+ "||createOrderReq:{}",
				createOrderReq);
		
		OrderResponse response = paymentService.createOrder(createOrderReq);
		log.info("Order creation response from service: {}", response);
		
		return response;
	}
	
	@PostMapping("/{orderId}/capture")
	public OrderResponse captureOrder(@PathVariable String orderId) {
		log.info("Capturing order in PayPal provider service"
				+ "||orderId:{}",
				orderId);
		
		OrderResponse response = paymentService.captureOrder(orderId);
		log.info("Order capture response from service: {}", response);
		
		return response;
	}

}
