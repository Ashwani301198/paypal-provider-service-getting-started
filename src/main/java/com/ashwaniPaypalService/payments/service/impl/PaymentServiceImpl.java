package com.ashwaniPaypalService.payments.service.impl;

import com.ashwaniPaypalService.payments.service.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.ashwaniPaypalService.payments.service.interfaces.PaymentService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final TokenService tokenService;

    @Override
    public String createOrder() {
       log.info("*****Creating order in PaymentServiceImpl");

     String accessToken =  tokenService .getAccessToken();
     log.info("*****Access token retrieved: {}", accessToken);

        return "*****Order created from service -" + accessToken;
    }

    @PostConstruct
    public void init() {
        log.info("*****PaymentServiceImpl initialized");
     }
}
