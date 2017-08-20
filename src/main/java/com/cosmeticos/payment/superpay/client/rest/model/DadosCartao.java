package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

@Data
public class DadosCartao {
    private String nomePortador;
    private String numeroCartao;
    private String codigoSeguranca;
    private String dataValidade;
    
}
