
package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class RetornoTransacao {

    public Integer numeroTransacao;
    public String codigoEstabelecimento;
    public Integer codigoFormaPagamento;
    public Integer valor;
    public Integer valorDesconto;
    public Integer parcelas;
    public Integer statusTransacao;
    public String autorizacao;
    public String codigoTransacaoOperadora;
    //public String dataTransacaoOperadora;
    public String dataAprovacaoOperadora;
    public String numeroComprovanteVenda;
    public String nsu;
    public String mensagemVenda;
    public String urlPagamento;
    public List<String> cartoesUtilizados = null;

}
