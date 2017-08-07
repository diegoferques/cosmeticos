package com.cosmeticos.payment.superpay.client.rest.model;

public class Endereco {

    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
    private String pais;

    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getCep() { return cep; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getPais() { return pais; }

    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public void setNumero(String numero) { this.numero = numero; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public void setCep(String cep) { this.cep = cep; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setPais(String pais) { this.pais = pais; }
}
