package com.ashwaniPaypalService.payments.controller;

import com.ashwaniPaypalService.payments.service.interfaces.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;

    @PostMapping("/payments")
    public String createOrder() {
        log.info ("*******Creating order in paypal service ...");

       String response = paymentService.createOrder();
       log.info ("***** Order creation response from service : {} ", response);

        return response;
    }

    @PostConstruct
    void init() {
        log.info("*******PaymentController initialized" + "paymentServiceImpl:{} ", paymentService);
     }

}
