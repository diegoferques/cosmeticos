package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

@Data
public class ItemPedido {

    private String codigoProduto;
    private String nomeProduto;
    private String codigoCategoria;
    private String nomeCategoria;
    private int quantidadeProduto;
    private long valorUnitarioProduto;
    
}
