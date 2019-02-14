package com.cosmeticos.controller;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

public class RequestHelper {

    public static ResponseEntity<?> postEntity(TestRestTemplate restTemplate, String url, String body, Class<?> responseClass) throws URISyntaxException {
        RequestEntity<String> entity = RequestEntity
                .post(new URI(url))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(body);

        ResponseEntity<?> exchange = restTemplate
                .exchange(entity, responseClass);

        return exchange;
    }
}
