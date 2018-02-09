package com.cosmeticos.controller;

import com.cosmeticos.commons.AddressResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by Vinicius on 22/06/2017.
 */
@Slf4j
@RestController
public class AddressController {
    @Autowired
    private AddressService addressService;

    @RequestMapping(path = "/addresses", method = RequestMethod.GET)
    public HttpEntity<AddressResponseBody> getByCep(
            @RequestParam(value = "cep", required = true) String cep
    ) {

        try {


            Address a = new Address();
            a.setAddress("Rua da Abolicao");
            a.setCep(cep);
            a.setCity("Nova Iguacu");
            a.setCountry("Brasil");
            a.setNeighborhood("Riachao");
            a.setState("RJ");

            AddressResponseBody responseBody = AddressResponseBody.builder().build();
            responseBody.setAddressList(new ArrayList<>());
            responseBody.getAddressList().add(a);

            log.info("Address successfully retrieved for cep {}.", cep);

            return ok().body(responseBody);

        } catch (Exception e) {
            log.error("Failed to retrieve ProfessionalServices: {}", e.getMessage(), e);

            return ResponseEntity.status(500).body(
                    AddressResponseBody.builder()
                            .description("Falha pesquisando CEP: " + e.getMessage())
                            .build()
            );
        }

    }
}
