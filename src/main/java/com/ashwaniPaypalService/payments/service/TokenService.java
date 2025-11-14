package com.ashwaniPaypalService.payments.service;

import com.ashwaniPaypalService.payments.constant.Constant;
import com.ashwaniPaypalService.payments.http.HttpRequest;
import com.ashwaniPaypalService.payments.http.HttpServiceEngine;
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

        return tokenBody;
    }
}
