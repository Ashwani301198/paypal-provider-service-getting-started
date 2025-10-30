package com.hulkhiretech.payments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hulkhiretech.payments.constant.Constant;
import com.hulkhiretech.payments.http.HttpRequest;
import com.hulkhiretech.payments.http.HttpServiceEngine;
import com.hulkhiretech.payments.paypal.res.PayPalOAuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final HttpServiceEngine httpServiceEngine;
    private static String accessToken;

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.oauth.url}")
    private String oAuth;

    private final ObjectMapper objectMapper;

    public String getAccessToken() {
        log.info("Retrieving access token from TokenService");
        if (accessToken != null) {
            log.info("returning cached token");
            return accessToken;
        }
        log.info("No cached token found, calling OAuth service ");

        HttpHeaders headers = new HttpHeaders();

        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setUrl(oAuth);
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setBody(formData);

        log.info("Prepared HttpRequest for OAuth call : {}", httpRequest);

        ResponseEntity<String> response = httpServiceEngine.makeHttpCall(httpRequest);
        log.info("HTTP response from httpServiceEngine: {}", response);

        String tokenBody = response.getBody();
        log .info("Access token retrieved {}", tokenBody);

        try {
            PayPalOAuthToken token = objectMapper.readValue(tokenBody, PayPalOAuthToken.class);
            log .info("Parsed OAuth token: {}", token);

            return token.getAccessToken();

        } catch (Exception e) {
            log.error("Error parsing access token response: {}", e.getMessage() , e);
            throw new RuntimeException("Failed to parse access token response", e);
        }

    }
}
