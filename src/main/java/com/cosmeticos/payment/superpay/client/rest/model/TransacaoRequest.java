package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class TransacaoRequest {

    private String codigoEstabelecimento;
    private int codigoFormaPagamento;
    private Transacao transacao;
    private DadosCartao dadosCartao;
    private List<ItemPedido> itensDoPedido;
    private DadosCobranca dadosCobranca;
    private DadosEntrega dadosEntrega;
    
}
