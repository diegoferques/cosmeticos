package com.cosmeticos.payment.superpay.client.rest.model;

public class DadosCartao {
    private String nomePortador;
    private String numeroCartao;
    private String codigoSeguranca;
    private String dataValidade;

    public String getNomePortador() { return nomePortador; }
    public String getNumeroCartao() { return numeroCartao; }
    public String getCodigoSeguranca() { return codigoSeguranca; }
    public String getDataValidade() { return dataValidade; }

    public void setNomePortador(String nomePortador) { this.nomePortador = nomePortador; }
    public void setNumeroCartao(String numeroCartao) { this.numeroCartao = numeroCartao; }
    public void setCodigoSeguranca(String codigoSeguranca) { this.codigoSeguranca = codigoSeguranca; }
    public void setDataValidade(String dataValidade) { this.dataValidade = dataValidade; }
    
}
