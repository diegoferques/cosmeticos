//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.11 às 01:09:11 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de transacaoDadosCobrancaCartaoVo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="transacaoDadosCobrancaCartaoVo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://pagamentos.webservices.superpay.ernet.com.br/}vo"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="indice" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="titular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="validadeAno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="validadeMes" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transacaoDadosCobrancaCartaoVo", propOrder = {
    "indice",
    "numero",
    "titular",
    "validadeAno",
    "validadeMes"
})
public class TransacaoDadosCobrancaCartaoVo
    extends Vo
{

    protected Long indice;
    protected String numero;
    protected String titular;
    protected String validadeAno;
    protected String validadeMes;

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIndice(Long value) {
        this.indice = value;
    }

    /**
     * Obtém o valor da propriedade numero.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumero(String value) {
        this.numero = value;
    }

    /**
     * Obtém o valor da propriedade titular.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitular() {
        return titular;
    }

    /**
     * Define o valor da propriedade titular.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitular(String value) {
        this.titular = value;
    }

    /**
     * Obtém o valor da propriedade validadeAno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidadeAno() {
        return validadeAno;
    }

    /**
     * Define o valor da propriedade validadeAno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidadeAno(String value) {
        this.validadeAno = value;
    }

    /**
     * Obtém o valor da propriedade validadeMes.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidadeMes() {
        return validadeMes;
    }

    /**
     * Define o valor da propriedade validadeMes.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidadeMes(String value) {
        this.validadeMes = value;
    }

}
