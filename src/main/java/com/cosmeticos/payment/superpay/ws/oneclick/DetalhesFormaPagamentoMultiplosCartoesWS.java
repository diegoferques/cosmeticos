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
 * <p>Classe Java de detalhesFormaPagamentoMultiplosCartoesWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="detalhesFormaPagamentoMultiplosCartoesWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="autorizacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="codigoFormaPagamento" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="codigoTransacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="dataAprovacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mensagemVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroComprovanteVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="parcelas" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="taxaEmbarque" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
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
@XmlType(name = "detalhesFormaPagamentoMultiplosCartoesWS", propOrder = {
    "autorizacao",
    "codigoFormaPagamento",
    "codigoTransacaoOperadora",
    "dataAprovacaoOperadora",
    "mensagemVenda",
    "numeroComprovanteVenda",
    "parcelas",
    "taxaEmbarque",
    "valor",
    "valorDesconto"
})
public class DetalhesFormaPagamentoMultiplosCartoesWS {

    protected int autorizacao;
    protected int codigoFormaPagamento;
    protected int codigoTransacaoOperadora;
    protected String dataAprovacaoOperadora;
    protected String mensagemVenda;
    protected String numeroComprovanteVenda;
    protected int parcelas;
    protected long taxaEmbarque;
    protected long valor;
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
