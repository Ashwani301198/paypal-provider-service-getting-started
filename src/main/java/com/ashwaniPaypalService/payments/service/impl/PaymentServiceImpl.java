package com.ashwaniPaypalService.payments.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ashwaniPaypalService.payments.http.HttpRequest;
import com.ashwaniPaypalService.payments.http.HttpServiceEngine;
import com.ashwaniPaypalService.payments.pojo.CreateOrderReq;
import com.ashwaniPaypalService.payments.pojo.OrderResponse;
import com.ashwaniPaypalService.payments.service.PaymentValidator;
import com.ashwaniPaypalService.payments.service.TokenService;
import com.ashwaniPaypalService.payments.service.helper.CaptureOrderHelper;
import com.ashwaniPaypalService.payments.service.helper.CreateOrderHelper;
import com.ashwaniPaypalService.payments.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final TokenService tokenService;
	
	private final HttpServiceEngine httpServiceEngine;
	
	@Value("${paypal.create.order.url}")
	private String createOrderUrl;
	
	private final CreateOrderHelper	createOrderHelper;
	
	private final CaptureOrderHelper captureOrderHelper;
	
	private final PaymentValidator paymentValidator;
	
	@Override
	public OrderResponse createOrder(CreateOrderReq createOrderReq) {
		log.info("Creating order in PaymentServiceImpl|| createOrderReq:{}",
				createOrderReq);
		
		paymentValidator.validateCreateOrder(createOrderReq);
		
		log.info("Create order request validated successfully");
		
		String accessToken = tokenService.getAccessToken();
		log.info("Access token retrieved: {}", accessToken);
		
		HttpRequest httpRequest = createOrderHelper.prepareCreateOrderHttpRequest(
				createOrderReq, accessToken);
		log.info("Prepared HttpRequest for OAuth call: {}", httpRequest);
		
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP response from HttpServiceEngine: {}", httpResponse);

		OrderResponse orderResponse = createOrderHelper.handlePaypalResponse(httpResponse);
		log.info("Final OrderResponse to be returned: {}", orderResponse);
		
		return orderResponse;
	}

	@Override
	public OrderResponse captureOrder(String orderId) {
		log.info("Capturing order in PaymentServiceImpl|| orderId:{}",
				orderId);
		
		String accessToken = tokenService.getAccessToken();
		log.info("Access token retrieved: {}", accessToken);
		
		HttpRequest httpRequest = captureOrderHelper.prepareCaptureOrderHttpRequest(
				orderId, accessToken);
		log.info("Prepared HttpRequest for capturing order httpRequest: {}", httpRequest);
		
		ResponseEntity<String> httpResponse = httpServiceEngine.makeHttpCall(httpRequest);
		log.info("HTTP response from HttpServiceEngine: {}", httpResponse);
		
		OrderResponse orderResponse = captureOrderHelper.handlePaypalResponse(httpResponse);
		log.info("Final OrderResponse to be returned: {}", orderResponse);
		
		return orderResponse;
	}

}
