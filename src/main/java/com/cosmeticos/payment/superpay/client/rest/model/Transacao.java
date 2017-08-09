package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

@Data
public class Transacao {
    
    private int parcelas;
    private int idioma;
    private String dataVencimentoBoleto;
    private String urlCampainha;
    private String urlResultado;
    private long valorDesconto;
    private long valor;
    private long numeroTransacao;
    
}
