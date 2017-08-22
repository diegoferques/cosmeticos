package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

@Data
public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
    private String pais;
}
