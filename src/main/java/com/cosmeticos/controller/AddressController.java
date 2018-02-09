package com.cosmeticos.controller;

import com.cosmeticos.commons.AddressResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by matto on 08/02/2018.
 */
@Slf4j
@RestController
public class AddressController {

    @RequestMapping(path = "/address", method = RequestMethod.GET)
    public HttpEntity<AddressResponseBody> findCepByWebService(@RequestParam("cep") String cep) {

        AddressResponseBody responseBody = new AddressResponseBody();

        if(cep.isEmpty()) {
            responseBody.setDescription("Cep não pode ser nulo ou vazio");

            log.error(responseBody.getDescription());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

        } else {

        }

    }
}
