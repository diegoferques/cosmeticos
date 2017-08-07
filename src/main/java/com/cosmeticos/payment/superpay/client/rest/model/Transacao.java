package com.cosmeticos.payment.superpay.client.rest.model;

public class Transacao {
    
    private int parcelas;
    private int idioma;
    private String dataVencimentoBoleto;
    private String urlCampainha;
    private String urlResultado;
    private long valorDesconto;
    private long valor;
    private long numeroTransacao;

    public int getParcelas() { return parcelas; }
    public int getIdioma() { return idioma; }
    public String getDataVencimentoBoleto() { return dataVencimentoBoleto; }
    public String getUrlCampainha() { return urlCampainha; }
    public String getUrlResultado() { return urlResultado; }
    public long getValorDesconto() { return valorDesconto; }
    public long getValor() { return valor; }
    public long getNumeroTransacao() { return numeroTransacao; }

    public void setParcelas(int parcelas) { this.parcelas = parcelas; }
    public void setIdioma(int idioma) { this.idioma = idioma; }
    public void setDataVencimentoBoleto(String dataVencimentoBoleto) { this.dataVencimentoBoleto = dataVencimentoBoleto; }
    public void setUrlCampainha(String urlCampainha) { this.urlCampainha = urlCampainha; }
    public void setUrlResultado(String urlResultado) { this.urlResultado = urlResultado; }
    public void setValorDesconto(long valorDesconto) { this.valorDesconto = valorDesconto; }
    public void setValor(long valor) { this.valor = valor; }
    public void setNumeroTransacao(long numeroTransacao) { this.numeroTransacao = numeroTransacao; }
    
}
