//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 03:52:44 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de operacaoVO complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="operacaoVO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="operacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "operacaoVO", propOrder = {
    "codigoEstabelecimento",
    "numeroTransacao",
    "operacao"
})
public class OperacaoVO {

    protected String codigoEstabelecimento;
    protected Long numeroTransacao;
    protected int operacao;

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
     * Obtém o valor da propriedade numeroTransacao.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumeroTransacao() {
        return numeroTransacao;
    }

    /**
     * Define o valor da propriedade numeroTransacao.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumeroTransacao(Long value) {
        this.numeroTransacao = value;
    }

    /**
     * Obtém o valor da propriedade operacao.
     * 
     */
    public int getOperacao() {
        return operacao;
    }

    /**
     * Define o valor da propriedade operacao.
     * 
     */
    public void setOperacao(int value) {
        this.operacao = value;
    }

}
