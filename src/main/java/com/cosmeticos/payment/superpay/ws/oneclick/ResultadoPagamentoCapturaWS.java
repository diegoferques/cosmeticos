//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 01:01:37 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.oneclick;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de resultadoPagamentoCapturaWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="resultadoPagamentoCapturaWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="autorizacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoFormaPagamento" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="codigoTransacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="dataAprovacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mensagemVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroComprovanteVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="parcelas" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="statusTransacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="taxaEmbarque" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="urlPagamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="valorCapturado" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="valorDesconto" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoPagamentoCapturaWS", propOrder = {
    "autorizacao",
    "codigoEstabelecimento",
    "codigoFormaPagamento",
    "codigoTransacaoOperadora",
    "dataAprovacaoOperadora",
    "mensagemVenda",
    "numeroComprovanteVenda",
    "numeroTransacao",
    "parcelas",
    "statusTransacao",
    "taxaEmbarque",
    "urlPagamento",
    "valor",
    "valorCapturado",
    "valorDesconto"
})
public class ResultadoPagamentoCapturaWS {

    protected int autorizacao;
    protected String codigoEstabelecimento;
    protected int codigoFormaPagamento;
    protected int codigoTransacaoOperadora;
    protected String dataAprovacaoOperadora;
    protected String mensagemVenda;
    protected String numeroComprovanteVenda;
    protected long numeroTransacao;
    protected int parcelas;
    protected int statusTransacao;
    protected long taxaEmbarque;
    protected String urlPagamento;
    protected long valor;
    protected long valorCapturado;
    protected long valorDesconto;

    /**
     * Obtém o valor da propriedade autorizacao.
     * 
     */
    public int getAutorizacao() {
        return autorizacao;
    }

    /**
     * Define o valor da propriedade autorizacao.
     * 
     */
    public void setAutorizacao(int value) {
        this.autorizacao = value;
    }

    /**
     * Obtém o valor da propriedade codigoEstabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    /**
     * Define o valor da propriedade codigoEstabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoEstabelecimento(String value) {
        this.codigoEstabelecimento = value;
    }

    /**
     * Obtém o valor da propriedade codigoFormaPagamento.
     * 
     */
    public int getCodigoFormaPagamento() {
        return codigoFormaPagamento;
    }

    /**
     * Define o valor da propriedade codigoFormaPagamento.
     * 
     */
    public void setCodigoFormaPagamento(int value) {
        this.codigoFormaPagamento = value;
    }

    /**
     * Obtém o valor da propriedade codigoTransacaoOperadora.
     * 
     */
    public int getCodigoTransacaoOperadora() {
        return codigoTransacaoOperadora;
    }

    /**
     * Define o valor da propriedade codigoTransacaoOperadora.
     * 
     */
    public void setCodigoTransacaoOperadora(int value) {
        this.codigoTransacaoOperadora = value;
    }

    /**
     * Obtém o valor da propriedade dataAprovacaoOperadora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataAprovacaoOperadora() {
        return dataAprovacaoOperadora;
    }

    /**
     * Define o valor da propriedade dataAprovacaoOperadora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataAprovacaoOperadora(String value) {
        this.dataAprovacaoOperadora = value;
    }

    /**
     * Obtém o valor da propriedade mensagemVenda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensagemVenda() {
        return mensagemVenda;
    }

    /**
     * Define o valor da propriedade mensagemVenda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensagemVenda(String value) {
        this.mensagemVenda = value;
    }

    /**
     * Obtém o valor da propriedade numeroComprovanteVenda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroComprovanteVenda() {
        return numeroComprovanteVenda;
    }

    /**
     * Define o valor da propriedade numeroComprovanteVenda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroComprovanteVenda(String value) {
        this.numeroComprovanteVenda = value;
    }

    /**
     * Obtém o valor da propriedade numeroTransacao.
     * 
     */
    public long getNumeroTransacao() {
        return numeroTransacao;
    }

    /**
     * Define o valor da propriedade numeroTransacao.
     * 
     */
    public void setNumeroTransacao(long value) {
        this.numeroTransacao = value;
    }

    /**
     * Obtém o valor da propriedade parcelas.
     * 
     */
    public int getParcelas() {
        return parcelas;
    }

    /**
     * Define o valor da propriedade parcelas.
     * 
     */
    public void setParcelas(int value) {
        this.parcelas = value;
    }

    /**
     * Obtém o valor da propriedade statusTransacao.
     * 
     */
    public int getStatusTransacao() {
        return statusTransacao;
    }

    /**
     * Define o valor da propriedade statusTransacao.
     * 
     */
    public void setStatusTransacao(int value) {
        this.statusTransacao = value;
    }

    /**
     * Obtém o valor da propriedade taxaEmbarque.
     * 
     */
    public long getTaxaEmbarque() {
        return taxaEmbarque;
    }

    /**
     * Define o valor da propriedade taxaEmbarque.
     * 
     */
    public void setTaxaEmbarque(long value) {
        this.taxaEmbarque = value;
    }

    /**
     * Obtém o valor da propriedade urlPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlPagamento() {
        return urlPagamento;
    }

    /**
     * Define o valor da propriedade urlPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlPagamento(String value) {
        this.urlPagamento = value;
    }

    /**
     * Obtém o valor da propriedade valor.
     * 
     */
    public long getValor() {
        return valor;
    }

    /**
     * Define o valor da propriedade valor.
     * 
     */
    public void setValor(long value) {
        this.valor = value;
    }

    /**
     * Obtém o valor da propriedade valorCapturado.
     * 
     */
    public long getValorCapturado() {
        return valorCapturado;
    }

    /**
     * Define o valor da propriedade valorCapturado.
     * 
     */
    public void setValorCapturado(long value) {
        this.valorCapturado = value;
    }

    /**
     * Obtém o valor da propriedade valorDesconto.
     * 
     */
    public long getValorDesconto() {
        return valorDesconto;
    }

    /**
     * Define o valor da propriedade valorDesconto.
     * 
     */
    public void setValorDesconto(long value) {
        this.valorDesconto = value;
    }

}
