package com.hulkhiretech.payments.controller;

import com.hulkhiretech.payments.pojo.CreateOrderReq;
import com.hulkhiretech.payments.pojo.OrderResponse;
import com.hulkhiretech.payments.service.interfaces.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public OrderResponse createOrder(@RequestBody CreateOrderReq createOrderReq) {
        log.info("Creating order in PayPal provider service"
                        + "||createOrderReq:{}",
                createOrderReq);

        OrderResponse response = paymentService.createOrder(createOrderReq);
        log.info("Order creation response from service: {}", response);

        return response;
    }

    @PostConstruct
    void init() {
        log.info("PaymentController initialized "
                + "paymentService:{}", paymentService);
    }

}
