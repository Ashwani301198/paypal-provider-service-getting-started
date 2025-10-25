package com.hulkhiretech.payments.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

    private final RestClient restClient;

    public String makeHttpCall() {
        log.info("Making HTTP call in HttpServiceEngine");

        HttpHeaders headers = new HttpHeaders();

        String clientId = "AWo5eGT8dJ8z4wv295DWA8WNSidMcTLlPxQDEr1ZusqkdbOIIELPPTcg-hGGWoadE5aljHJCgl5ZB_sN";
        String clientSecret = "EBkqh3eJB82fK3r0H4heuxG6wUrcHrzBlmOBAT1vREmfMUBtVw5_JjYtdJTjqME3LjP4SDtDnCaFqlen";

        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

class ConsumerHeaderObj implements Consumer<HttpHeaders> {

    HttpHeaders applicationHeaders;

    ConsumerHeaderObj(HttpHeaders applicationHeaders) {
        this.applicationHeaders = applicationHeaders;
    }

    @Override
    public void accept(HttpHeaders restClientHeaders) {
        restClientHeaders.addAll(this.applicationHeaders);

    }
}

        MultiValueMap<String , String> formData = new LinkedMultiValueMap<>();
       // formData.add("grant_type", "client_credentials");
 
        String httpResponse = restClient.method(HttpMethod.POST)
                .uri("https://api-m.sandbox.paypal.com/v1/oauth2/token")
                .headers(new  ConsumerHeaderObj(headers))
                .body(formData)
                .retrieve()
                .body(String.class);

                log.info("HTTP call completed in HttpResponse : {}" ,httpResponse );

        return httpResponse;
    }
}
