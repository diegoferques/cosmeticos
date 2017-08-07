package com.cosmeticos.payment.superpay.client.rest.model;

public class Telefone {
    private Integer tipoTelefone;
    private String ddi;
    private String ddd;
    private String telefone;

    public Integer getTipoTelefone() { return tipoTelefone; }
    public String getDdi() { return ddi; }
    public String getDdd() { return ddd; }
    public String getTelefone() { return telefone; }

    public void setTipoTelefone(Integer tipoTelefone) { this.tipoTelefone = tipoTelefone; }
    public void setDdi(String ddi) { this.ddi = ddi; }
    public void setDdd(String ddd) { this.ddd = ddd; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
}
