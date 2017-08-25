package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Erro {
    private String codigo;
    private String mensagem;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}
