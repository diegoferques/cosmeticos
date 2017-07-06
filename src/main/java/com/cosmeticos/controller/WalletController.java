package com.cosmeticos.controller;

import com.cosmeticos.commons.WalletResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.Wallet;
import com.cosmeticos.service.WalletService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.*;

@Slf4j
@RestController
public class WalletController {

    @Autowired
    private WalletService service;

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @RequestMapping(path = "/wallets", method = RequestMethod.GET)
    public HttpEntity<WalletResponseBody> findAll(@ModelAttribute Wallet wallet) {

        try {
            List<Wallet> entityList = service.findAllBy(wallet);

            if(entityList.isEmpty())
            {
                log.error(entityList.size() + " wallets retrieved: http status code 404");
                return ResponseEntity.notFound().build();
            }
            else {
                WalletResponseBody responseBody = new WalletResponseBody();
                responseBody.getWalletList().addAll(entityList);
                responseBody.setDescription(entityList.size() + " wallet succesfully retrieved.");

                log.info(entityList.size() + " wallet succesfully retrieved.");

                return ok().body(responseBody);
            }
        } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            WalletResponseBody responseBody = new WalletResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }
    }
}
