package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.Date;

@Data
public class DadosCartao {
    private String nomePortador;
    private String numeroCartao;
    private String codigoSeguranca;
    private Date dataValidade;
    
}
