package com.hulkhiretech.payments.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.req.*;
import com.hulkhiretech.payments.service.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TokenService tokenService;

    @Override
    public String createOrder() {
        log.info("*****Creating order in PaymentServiceImpl");

        // Step 1: Retrieve access token
        String accessToken = tokenService.getAccessToken();
        log.info("*****Access token retrieved: {}", accessToken);

        // Step 2: Setup headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uuid = UUID.randomUUID().toString();
        log.info("Generated UUID for PayPal-Request-Id: {}", uuid);
        headers.add("PayPal-Request-Id", uuid);

        // Step 3: Set amount and purchase unit
        Amount amount = new Amount();
        amount.setCurrency_code("USD");
        amount.setValue("1.00");

        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        // Step 4: Set experience context
        ExperienceContext ctx = new ExperienceContext();
        ctx.setPaymentMethodPreference("IMMEDIATE_PAYMENT_REQUIRED");
        ctx.setLandingPage("LOGIN");
        ctx.setShippingPreference("NO_SHIPPING");
        ctx.setUserAction("PAY_NOW");
        ctx.setReturnUrl("https://example.com/returnUrl");
        ctx.setCancelUrl("https://example.com/cancelUrl");

        // Step 5: Set PayPal payment source
        Paypal paypal = new Paypal();
        paypal.setExperienceContext(ctx);

        PaymentSource ps = new PaymentSource();
        ps.setPaypal(paypal);

        // Step 6: Build order request
        OrderRequest order = new OrderRequest();
        order.setIntent("CAPTURE");
        order.setPurchaseUnits(Collections.singletonList(unit));
        order.setPaymentSource(ps);

        // Step 7: Convert to JSON
        ObjectMapper mapper = new ObjectMapper();

        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(order);
            log.info("OrderRequest JSON: {}", json);
            return json;
        } catch (JsonProcessingException e) {
            log.error("Error creating OrderRequest JSON", e);
            throw new RuntimeException("Error creating OrderRequest JSON", e);
        }

        // Step 8 (optional, unreachable): Prepare HttpRequest for future use
        /*
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setBody(formData);

        log.info("Prepared HttpRequest for OAuth call : {}", httpRequest);
        */
    }

    @PostConstruct
    public void init() {
        log.info("*****PaymentServiceImpl initialized");
    }
}
