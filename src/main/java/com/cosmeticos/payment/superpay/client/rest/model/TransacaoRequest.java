package com.cosmeticos.payment.superpay.client.rest.model;

import java.util.List;

public class TransacaoRequest {

    private String codigoEstabelecimento;
    private int codigoFormaPagamento;
    private Transacao transacao;
    private DadosCartao dadosCartao;
    private List<ItemPedido> itensDoPedido;
    private DadosCobranca dadosCobranca;
    private DadosEntrega dadosEntrega;

    public String getCodigoEstabelecimento() { return codigoEstabelecimento; }
    public int getCodigoFormaPagamento() { return codigoFormaPagamento; }
    public Transacao getTransacao() { return transacao; }
    public DadosCartao getDadosCartao() { return dadosCartao; }
    public List<ItemPedido> getItensDoPedido() { return itensDoPedido; }
    public DadosCobranca getDadosCobranca() { return dadosCobranca; }
    public DadosEntrega getDadosEntrega() { return dadosEntrega; }

    public void setCodigoEstabelecimento (String codigoEstabelecimento) { this.codigoEstabelecimento = codigoEstabelecimento; }
    public void setCodigoFormaPagamento (int codigoFormaPagamento) { this.codigoFormaPagamento = codigoFormaPagamento; }
    public void setTransacao (Transacao transacao) { this.transacao = transacao; }
    public void setDadosCartao (DadosCartao dadosCartao) { this.dadosCartao = dadosCartao; }
    public void setItensDoPedido (List<ItemPedido> itensDoPedido) { this.itensDoPedido = itensDoPedido; }
    public void setDadosCobranca (DadosCobranca dadosCobranca) { this.dadosCobranca = dadosCobranca; }
    public void setDadosEntrega (DadosEntrega dadosEntrega) { this.dadosEntrega = dadosEntrega; }
    
}
