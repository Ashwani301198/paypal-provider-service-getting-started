package com.hulkhiretech.payments.service.helper;

import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.paypal.req.*;
import com.hulkhiretech.payments.paypal.res.PaypalLink;
import com.hulkhiretech.payments.paypal.res.PaypalOrder;
import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

/**
 * Helper class to prepare PayPal Create Order requests.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {

    private final JsonUtil jsonUtil;

    @Value("${paypal.create.order.url}")
    private String createOrderUrl;

    /**
     * Prepares HttpRequest for creating a PayPal order.
     */
    public HttpRequest prepareCreateOrderHttpRequest(CreateOrderReq createOrderReq, String accessToken) {
        HttpHeaders headers = prepareHeader(accessToken);
        String requestAsJson = prepareReqBodyAsJson(createOrderReq);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(createOrderUrl);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setBody(requestAsJson);

        log.info("Prepared CreateOrder HttpRequest: {}", httpRequest);
        return httpRequest;
    }

    /**
     * Builds JSON request body for PayPal Create Order API.
     */
    private String prepareReqBodyAsJson(CreateOrderReq createOrderReq) {
        // Create amount object
        Amount amount = new Amount();
        amount.setCurrencyCode(createOrderReq.getCurrencyCode());

        // Convert to two-decimal format
        String amtStr = String.format(Constant.TWO_DECIMAL_FORMAT, createOrderReq.getAmount());
        amount.setValue(amtStr);

        // Create purchase unit
        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        // Experience context setup
        ExperienceContext ctx = new ExperienceContext();
        ctx.setPaymentMethodPreference(Constant.IMMEDIATE_PAYMENT_REQUIRED);
        ctx.setLandingPage(Constant.LANDINGPAGE_LOGIN);
        ctx.setShippingPreference(Constant.SHIPPING_PREF_NO_SHIPPING);
        ctx.setUserAction(Constant.USER_ACTION_PAY_NOW);
        ctx.setReturnUrl(createOrderReq.getReturnUrl());
        ctx.setCancelUrl(createOrderReq.getCancelUrl());

        // Paypal object
        Paypal paypal = new Paypal();
        paypal.setExperienceContext(ctx);

        // Payment source
        PaymentSource ps = new PaymentSource();
        ps.setPaypal(paypal);

        // Final order request
        OrderRequest order = new OrderRequest();
        order.setIntent(Constant.INTENT_CAPTURE);
        order.setPurchaseUnits(Collections.singletonList(unit));
        order.setPaymentSource(ps);

        log.info("Constructed OrderRequest object: {}", order);

        // Convert to JSON string
        return jsonUtil.toJson(order);
    }

    /**
     * Prepare authorization headers for PayPal API.
     */
    private HttpHeaders prepareHeader(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Generate PayPal-Request-Id header
        String uuid = UUID.randomUUID().toString();
        log.info("Generated UUID for PayPal-Request-Id: {}", uuid);
        headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);

        return headers;
    }

    /**
     * Converts PayPal API response to internal OrderResponse object.
     */
    public OrderResponse toOrderResponse(PaypalOrder paypalOrder) {
        log.info("Converting PaypalOrder to OrderResponse: {}", paypalOrder);

        OrderResponse response = new OrderResponse();
        response.setOrderId(paypalOrder.getId());
        response.setPaypalStatus(paypalOrder.getStatus());

        String redirectLink = paypalOrder.getLinks().stream()
                .filter(link -> "payer-action".equalsIgnoreCase(link.getRel()))
                .findFirst()
                .map(PaypalLink::getHref)
                .orElse(null);

        response.setRedirectUrl(redirectLink);

        log.info("Converted PaypalOrder to OrderResponse: {}", response);

        return response;
    }
}
