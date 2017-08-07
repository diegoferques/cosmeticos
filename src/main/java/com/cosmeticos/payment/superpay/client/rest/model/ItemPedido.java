package com.cosmeticos.payment.superpay.client.rest.model;

public class ItemPedido {

    private String codigoProduto;
    private String nomeProduto;
    private String codigoCategoria;
    private String nomeCategoria;
    private int quantidadeProduto;
    private long valorUnitarioProduto;

    public String getCodigoProduto() { return codigoProduto; }
    public String getNomeProduto() { return nomeProduto; }
    public String getCodigoCategoria() { return codigoCategoria; }
    public String getNomeCategoria() { return nomeCategoria; }
    public int getQuantidadeProduto() { return quantidadeProduto; }
    public long getValorUnitarioProduto() { return valorUnitarioProduto; }

    public void setCodigoProduto(String codigoProduto) { this.codigoProduto = codigoProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public void setCodigoCategoria(String codigoCategoria) { this.codigoCategoria = codigoCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
    public void setQuantidadeProduto(int quantidadeProduto) { this.quantidadeProduto = quantidadeProduto; }
    public void setValorUnitarioProduto(long valorUnitarioProduto) { this.valorUnitarioProduto = valorUnitarioProduto; }
    
}
