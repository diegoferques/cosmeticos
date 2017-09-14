//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.11 às 01:09:11 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de MultipleCardsException complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="MultipleCardsException"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cartoesVo" type="{http://pagamentos.webservices.superpay.ernet.com.br/}transacaoDadosCobrancaCartaoVo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cartao" type="{http://pagamentos.webservices.superpay.ernet.com.br/}transacaoDadosCobrancaCartaoVo"/&gt;
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultipleCardsException", propOrder = {
    "content"
})
public class MultipleCardsException {

    @XmlElementRefs({
        @XmlElementRef(name = "cartao", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "message", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "cartoesVo", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "key", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<?>> content;

    /**
     * Obtém o restante do modelo do conteúdo. 
     * 
     * <p>
     * Você está obtendo esta propriedade "catch-all" pelo seguinte motivo: 
     * O nome do campo "Message" é usado por duas partes diferentes de um esquema. Consulte: 
     * linha 804 de file:/C:/Users/magarrett.dias/Documents/Garry/estudos/projetos/cosmeticos/src/main/resources/wsdl/servicosPagamentoCompletoWS.Services.wsdl
     * linha 800 de file:/C:/Users/magarrett.dias/Documents/Garry/estudos/projetos/cosmeticos/src/main/resources/wsdl/servicosPagamentoCompletoWS.Services.wsdl
     * <p>
     * Para eliminar esta propriedade, aplique uma personalização de propriedade a uma 
     * das seguintes declarações, a fim de alterar seus nomes: 
     * Gets the value of the content property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the content property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link TransacaoDadosCobrancaCartaoVo }{@code >}
     * {@link JAXBElement }{@code <}{@link TransacaoDadosCobrancaCartaoVo }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * 
     * 
     */
    public List<JAXBElement<?>> getContent() {
        if (content == null) {
            content = new ArrayList<JAXBElement<?>>();
        }
        return this.content;
    }

}
