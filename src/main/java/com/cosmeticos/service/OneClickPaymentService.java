package com.cosmeticos.service;

import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.commons.ErrorCode;
import com.cosmeticos.controller.PaymentController;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.payment.SuperpayOneClickClient;
import com.cosmeticos.payment.superpay.client.rest.model.*;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class OneClickPaymentService implements Charger<Order, RetornoTransacao> {

    @Autowired
    private SuperpayOneClickClient oneClickClient;

    @Override
    public ChargeResponse<RetornoTransacao> addCard(ChargeRequest<Order> chargeRequest) {

        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        return null;
    }

    @Override
    public ChargeResponse<RetornoTransacao> reserve(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        return null;
    }

    @Override
    public ChargeResponse<RetornoTransacao> capture(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        return null;
    }

    @Override
    public ChargeResponse<RetornoTransacao> getStatus(ChargeRequest<Order> chargeRequest) {
        // TODO o que esta implmentado no teste SuperpayOneClickClientIntegrationTest deve ser reproduzido aqui para o metodo addCard
        return null;
    }
}
