package com.cosmeticos.controller;

import com.cosmeticos.commons.AddressResponseBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by matto on 08/02/2018.
 */
@Slf4j
@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @RequestMapping(path = "/addresses", method = RequestMethod.GET)
    public HttpEntity<AddressResponseBody> findCepByWebService(@RequestParam("cep") String cep) {

        AddressResponseBody responseBody = new AddressResponseBody();

        if(cep.isEmpty()) {
            responseBody.setDescription("Cep não pode ser nulo ou vazio");

            log.error(responseBody.getDescription());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

        } else {

            try {

                Optional<Address> addressOptional = addressService.findCepByWebService(cep);

                if(addressOptional.isPresent()) {

                    Address address = addressOptional.get();

                    responseBody.setAddress(address);

                    log.info("Busca de Address no WebService pelo cep com exito: [{}]", address);

                    return ResponseEntity.status(HttpStatus.OK).body(responseBody);

                } else {

                    responseBody.setDescription("Nenhum Address encontrado com o CEP informado");

                    log.error("Nenhum Address encontrado com o CEP informado: {}", cep);

                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
                }

            } catch (Exception e) {

                String errorCode = String.valueOf(System.nanoTime());

                responseBody.setDescription("Houve um erro interno no servidor: " + errorCode);

                log.error("Erro na exibição do Address solicitado com o cep: {} - {} - {}",
                        cep, errorCode, e.getMessage(), e);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
            }

        }

    }
}
