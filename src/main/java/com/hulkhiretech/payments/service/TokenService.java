package com.hulkhiretech.payments.service;

import com.hulkhiretech.payments.http.HttpServiceEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final HttpServiceEngine httpServiceEngine;
    private static String accessToken;

    public String getAccessToken() {
        log.info("Retrieving access token from TokenService");
        if (accessToken != null) {
            log.info("returning cached token");
            return accessToken;
        }
        log.info("No cached token found, calling OAuth service ");

        String response = httpServiceEngine.makeHttpCall();
        log.info("HTTP response from httpServiceEngine: {}", response);

        return "access_token - " + response;
    }
}
