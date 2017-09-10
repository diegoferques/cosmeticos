//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 01:01:31 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de dadosCartoesCredito complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="dadosCartoesCredito"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoFormaPagamento" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="codigoSeguranca" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataValidadeCartao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nomeTitularCartaoCredito" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroCartaoCredito" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="parcelas" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dadosCartoesCredito", propOrder = {
    "codigoFormaPagamento",
    "codigoSeguranca",
    "dataValidadeCartao",
    "nomeTitularCartaoCredito",
    "numeroCartaoCredito",
    "parcelas",
    "valor"
})
public class DadosCartoesCredito {

    protected int codigoFormaPagamento;
    protected String codigoSeguranca;
    protected String dataValidadeCartao;
    protected String nomeTitularCartaoCredito;
    protected String numeroCartaoCredito;
    protected int parcelas;
    protected Float valor;

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
     * Obtém o valor da propriedade valor.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getValor() {
        return valor;
    }

    /**
     * Define o valor da propriedade valor.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setValor(Float value) {
        this.valor = value;
    }

}
