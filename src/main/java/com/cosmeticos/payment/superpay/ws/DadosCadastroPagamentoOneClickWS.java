//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.06 às 06:46:41 PM BRT 
//


package com.cosmeticos.payment.superpay.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de dadosCadastroPagamentoOneClickWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="dadosCadastroPagamentoOneClickWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoSeguranca" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataValidadeCartao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="emailComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="formaPagamento" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="nomeTitularCartaoCredito" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroCartaoCredito" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dadosCadastroPagamentoOneClickWS", propOrder = {
    "codigoEstabelecimento",
    "codigoSeguranca",
    "dataValidadeCartao",
    "emailComprador",
    "formaPagamento",
    "nomeTitularCartaoCredito",
    "numeroCartaoCredito"
})
public class DadosCadastroPagamentoOneClickWS {

    protected String codigoEstabelecimento;
    protected String codigoSeguranca;
    protected String dataValidadeCartao;
    protected String emailComprador;
    protected Long formaPagamento;
    protected String nomeTitularCartaoCredito;
    protected String numeroCartaoCredito;

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
     * Obtém o valor da propriedade codigoSeguranca.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoSeguranca() {
        return codigoSeguranca;
    }

    /**
     * Define o valor da propriedade codigoSeguranca.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoSeguranca(String value) {
        this.codigoSeguranca = value;
    }

    /**
     * Obtém o valor da propriedade dataValidadeCartao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataValidadeCartao() {
        return dataValidadeCartao;
    }

    /**
     * Define o valor da propriedade dataValidadeCartao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataValidadeCartao(String value) {
        this.dataValidadeCartao = value;
    }

    /**
     * Obtém o valor da propriedade emailComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailComprador() {
        return emailComprador;
    }

    /**
     * Define o valor da propriedade emailComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailComprador(String value) {
        this.emailComprador = value;
    }

    /**
     * Obtém o valor da propriedade formaPagamento.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFormaPagamento() {
        return formaPagamento;
    }

    /**
     * Define o valor da propriedade formaPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFormaPagamento(Long value) {
        this.formaPagamento = value;
    }

    /**
     * Obtém o valor da propriedade nomeTitularCartaoCredito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeTitularCartaoCredito() {
        return nomeTitularCartaoCredito;
    }

    /**
     * Define o valor da propriedade nomeTitularCartaoCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeTitularCartaoCredito(String value) {
        this.nomeTitularCartaoCredito = value;
    }

    /**
     * Obtém o valor da propriedade numeroCartaoCredito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroCartaoCredito() {
        return numeroCartaoCredito;
    }

    /**
     * Define o valor da propriedade numeroCartaoCredito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroCartaoCredito(String value) {
        this.numeroCartaoCredito = value;
    }

}
