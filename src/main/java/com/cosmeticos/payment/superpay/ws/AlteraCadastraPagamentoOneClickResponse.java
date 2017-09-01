//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.08.31 às 03:17:48 PM BRT 
//


package com.cosmeticos.payment.superpay.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de alteraCadastraPagamentoOneClickResponse complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="alteraCadastraPagamentoOneClickResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://pagamentos.webservices.superpay.ernet.com.br/}dadosCadastroPagamentoOneClickWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "alteraCadastraPagamentoOneClickResponse", propOrder = {
    "_return"
})
public class AlteraCadastraPagamentoOneClickResponse {

    @XmlElement(name = "return")
    protected DadosCadastroPagamentoOneClickWS _return;

    /**
     * Obtém o valor da propriedade return.
     * 
     * @return
     *     possible object is
     *     {@link DadosCadastroPagamentoOneClickWS }
     *     
     */
    public DadosCadastroPagamentoOneClickWS getReturn() {
        return _return;
    }

    /**
     * Define o valor da propriedade return.
     * 
     * @param value
     *     allowed object is
     *     {@link DadosCadastroPagamentoOneClickWS }
     *     
     */
    public void setReturn(DadosCadastroPagamentoOneClickWS value) {
        this._return = value;
    }

}
