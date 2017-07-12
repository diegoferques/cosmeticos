package com.cosmeticos.controller;

import com.cosmeticos.commons.CreditCardResponseBody;
import com.cosmeticos.commons.ProfessionalServicesResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.ProfessionalServices;
import com.cosmeticos.service.CreditCardService;
import com.cosmeticos.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import netscape.security.ForbiddenTargetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by Vinicius on 07/07/2017.
 */
@Slf4j
@RestController
public class CreditCardController {

    @Autowired
    private CreditCardService service;

    @Autowired
    private UserService userService;

    //@JsonView(ResponseJsonView.CreditCardFindAll.class)
    @RequestMapping(path = "/creditCard", method = RequestMethod.GET)
    public HttpEntity<CreditCardResponseBody> findAll(@ModelAttribute CreditCard ccAtributeAUX)
    {

        try
        {
            List<CreditCard> entitylist = service.findAllBy(ccAtributeAUX);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();

                if (entitylist.isEmpty()) {
                    return notFound().build();
                } else {
                    //CreditCardResponseBody responseBody = new CreditCardResponseBody();
                    responseBody.setCreditCardList(entitylist);
                    responseBody.setDescription("Success");

                    log.info("{} CreditCard successfully retrieved.", entitylist.size());

                    return ok().body(responseBody);
                }

            } catch (Exception e) {
            log.error("Falha no BUSCAR TODOS: {}", e.getMessage(), e);
            CreditCardResponseBody responseBody = new CreditCardResponseBody();
            responseBody.setDescription(e.getMessage());
            return ResponseEntity.status(500).body(responseBody);
        }

    }
}
