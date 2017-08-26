package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Data
public class RetornoCaptura {

    private Integer numeroTransacao;
    private String codigoEstabelecimento;
    private Integer codigoFormaPagamento;
    private Integer valor;
    private Integer valorDesconto;
    private Integer parcelas;
    private Integer statusTransacao;
    private String autorizacao;
    private String codigoTransacaoOperadora;
    private String dataAprovacaoOperadora;
    private String numeroComprovanteVenda;
    private String nsu;
    private String mensagemVenda;
    private String urlPagamento;
    private List<String> cartoesUtilizados = null;
    private Erro erro;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}
