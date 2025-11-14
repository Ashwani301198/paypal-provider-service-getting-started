package com.ashwaniPaypalService.payments.service.helper;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.ashwaniPaypalService.payments.constant.Constant;
import com.ashwaniPaypalService.payments.http.HttpRequest;
import com.ashwaniPaypalService.payments.paypal.req.Amount;
import com.ashwaniPaypalService.payments.paypal.req.ExperienceContext;
import com.ashwaniPaypalService.payments.paypal.req.OrderRequest;
import com.ashwaniPaypalService.payments.paypal.req.PaymentSource;
import com.ashwaniPaypalService.payments.paypal.req.Paypal;
import com.ashwaniPaypalService.payments.paypal.req.PurchaseUnit;
import com.ashwaniPaypalService.payments.paypal.res.PaypalLink;
import com.ashwaniPaypalService.payments.paypal.res.PaypalOrder;
import com.ashwaniPaypalService.payments.pojo.CreateOrderReq;
import com.ashwaniPaypalService.payments.pojo.OrderResponse;
import com.ashwaniPaypalService.payments.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateOrderHelper {

	private final JsonUtil jsonUtil;

	@Value("${paypal.create.order.url}")
	private String createOrderUrl;

	public HttpRequest prepareCreateOrderHttpRequest(
			CreateOrderReq createOrderReq, String accessToken) {
		HttpHeaders headers = prepareHeader(accessToken);

		String requestAsJson = prepareReqBodyAsJson(createOrderReq);

		// create HttpRequest
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);

		httpRequest.setUrl(createOrderUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(requestAsJson);
		return httpRequest;
	}

	private String prepareReqBodyAsJson(CreateOrderReq createOrderReq) {
		// Create amount object
		Amount amount = new Amount();
		amount.setCurrencyCode(createOrderReq.getCurrencyCode());

		// read the amount from createOrderReq and convert to 2 decimal places format string
		String amtStr = String.format(Constant.TWO_DECIMAL_FORMAT, createOrderReq.getAmount());
		amount.setValue(amtStr);

		// Create purchase unit
		PurchaseUnit unit = new PurchaseUnit();
		unit.setAmount(amount);

		// Experience context
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
		String requestAsJson = jsonUtil.toJson(order);
		return requestAsJson;
	}

	private HttpHeaders prepareHeader(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);

		// set header PayPal-Request-Id => UUID
		String uuid = UUID.randomUUID().toString();
		log.info("Generated UUID for PayPal-Request-Id: {}", uuid);

		headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);
		return headers;
	}
	
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
