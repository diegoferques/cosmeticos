//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.15 às 08:25:31 AM BRT 
//


package com.cosmeticos.payment.superpay.ws.oneclick;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de itemPedidoTransacaoWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="itemPedidoTransacaoWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoCategoria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoProduto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nomeCategoria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nomeProduto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="quantidadeProduto" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="valorUnitarioProduto" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemPedidoTransacaoWS", propOrder = {
    "codigoCategoria",
    "codigoProduto",
    "nomeCategoria",
    "nomeProduto",
    "quantidadeProduto",
    "valorUnitarioProduto"
})
public class ItemPedidoTransacaoWS {

    protected String codigoCategoria;
    protected String codigoProduto;
    protected String nomeCategoria;
    protected String nomeProduto;
    protected int quantidadeProduto;
    protected Long valorUnitarioProduto;

    /**
     * Obtém o valor da propriedade codigoCategoria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    /**
     * Define o valor da propriedade codigoCategoria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCategoria(String value) {
        this.codigoCategoria = value;
    }

    /**
     * Obtém o valor da propriedade codigoProduto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoProduto() {
        return codigoProduto;
    }

    /**
     * Define o valor da propriedade codigoProduto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoProduto(String value) {
        this.codigoProduto = value;
    }

    /**
     * Obtém o valor da propriedade nomeCategoria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeCategoria() {
        return nomeCategoria;
    }

    /**
     * Define o valor da propriedade nomeCategoria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeCategoria(String value) {
        this.nomeCategoria = value;
    }

    /**
     * Obtém o valor da propriedade nomeProduto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeProduto() {
        return nomeProduto;
    }

    /**
     * Define o valor da propriedade nomeProduto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeProduto(String value) {
        this.nomeProduto = value;
    }

    /**
     * Obtém o valor da propriedade quantidadeProduto.
     * 
     */
    public int getQuantidadeProduto() {
        return quantidadeProduto;
    }

    /**
     * Define o valor da propriedade quantidadeProduto.
     * 
     */
    public void setQuantidadeProduto(int value) {
        this.quantidadeProduto = value;
    }

    /**
     * Obtém o valor da propriedade valorUnitarioProduto.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getValorUnitarioProduto() {
        return valorUnitarioProduto;
    }

    /**
     * Define o valor da propriedade valorUnitarioProduto.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setValorUnitarioProduto(Long value) {
        this.valorUnitarioProduto = value;
    }

}
