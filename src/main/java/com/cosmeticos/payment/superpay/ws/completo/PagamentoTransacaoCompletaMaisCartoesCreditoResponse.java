//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 03:52:44 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de pagamentoTransacaoCompletaMaisCartoesCreditoResponse complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="pagamentoTransacaoCompletaMaisCartoesCreditoResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://pagamentos.webservices.superpay.ernet.com.br/}resultadoPagamentoMultiplosCartoesWS" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pagamentoTransacaoCompletaMaisCartoesCreditoResponse", propOrder = {
    "_return"
})
public class PagamentoTransacaoCompletaMaisCartoesCreditoResponse {

    @XmlElement(name = "return")
    protected ResultadoPagamentoMultiplosCartoesWS _return;

    /**
     * Obtém o valor da propriedade return.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoPagamentoMultiplosCartoesWS }
     *     
     */
    public ResultadoPagamentoMultiplosCartoesWS getReturn() {
        return _return;
    }

    /**
     * Define o valor da propriedade return.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoPagamentoMultiplosCartoesWS }
     *     
     */
    public void setReturn(ResultadoPagamentoMultiplosCartoesWS value) {
        this._return = value;
    }

}