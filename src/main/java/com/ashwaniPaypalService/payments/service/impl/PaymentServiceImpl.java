package com.ashwaniPaypalService.payments.service.impl;

import com.ashwaniPaypalService.payments.http.HttpRequest;
import com.ashwaniPaypalService.payments.http.HttpServiceEngine;
import com.ashwaniPaypalService.payments.paypal.res.PaypalOrder;
import com.ashwaniPaypalService.payments.pojo.CreateOrderReq;
import com.ashwaniPaypalService.payments.pojo.OrderResponse;
import com.ashwaniPaypalService.payments.service.TokenService;
import com.ashwaniPaypalService.payments.service.helper.CreateOrderHelper;
import com.ashwaniPaypalService.payments.service.interfaces.PaymentService;
import com.ashwaniPaypalService.payments.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TokenService tokenService;
    private final HttpServiceEngine httpServiceEngine;
    private final JsonUtil jsonUtil;
    private final CreateOrderHelper createOrderHelper;

    @Value("${paypal.create.order.url}")
    private String createOrderUrl;

    @Override
    public OrderResponse createOrder(CreateOrderReq createOrderReq) {
        log.info("Creating PayPal order in PaymentServiceImpl || Request: {}", createOrderReq);

        // Step 1: Get Access Token
        String accessToken = tokenService.getAccessToken();
        log.info("Access token retrieved successfully.");

        // Step 2: Prepare HTTP request for PayPal Create Order
        HttpRequest httpRequest = createOrderHelper.prepareCreateOrderHttpRequest(createOrderReq, accessToken);
        log.info("Prepared HttpRequest for PayPal Create Order: {}", httpRequest);

        // Step 3: Make API call to PayPal
        ResponseEntity<String> responseEntity = httpServiceEngine.makeHttpCall(httpRequest);
        log.info("Received response from PayPal API: {}", responseEntity);

        // Step 4: Parse JSON response to PaypalOrder
        String responseBody = responseEntity.getBody();
        if (responseBody == null || responseBody.isEmpty()) {
            log.error("Empty response received from PayPal API");
            throw new RuntimeException("Failed to create PayPal order: empty response");
        }

        PaypalOrder paypalOrder = jsonUtil.fromJson(responseBody, PaypalOrder.class);
        log.info("Converted JSON response to PaypalOrder: {}", paypalOrder);

        // Step 5: Convert to internal OrderResponse
        OrderResponse orderResponse = createOrderHelper.toOrderResponse(paypalOrder);
        log.info("Converted to internal OrderResponse: {}", orderResponse);

        return orderResponse;
    }
}
