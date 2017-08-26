
package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class RetornoTransacao {

    private Integer numeroTransacao;
    private String codigoEstabelecimento;
    private Integer codigoFormaPagamento;
    private Integer valor;
    private Integer valorDesconto;
    private Integer parcelas;
    private Integer statusTransacao;
    private String autorizacao;
    private String codigoTransacaoOperadora;
//  private String dataTransacaoOperadora;
    private String dataAprovacaoOperadora;
    private String numeroComprovanteVenda;
    private String nsu;
    private String mensagemVenda;
    private String urlPagamento;
    private List<String> cartoesUtilizados = null;

}