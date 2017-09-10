//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 01:01:31 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de resultadoPagamentoMultiplosCartoesWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="resultadoPagamentoMultiplosCartoesWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="detalhesFormaPagamentoMultiplosCartoes" type="{http://pagamentos.webservices.superpay.ernet.com.br/}detalhesFormaPagamentoMultiplosCartoesWS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="statusTransacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoPagamentoMultiplosCartoesWS", propOrder = {
    "codigoEstabelecimento",
    "detalhesFormaPagamentoMultiplosCartoes",
    "numeroTransacao",
    "statusTransacao"
})
public class ResultadoPagamentoMultiplosCartoesWS {

    protected String codigoEstabelecimento;
    @XmlElement(nillable = true)
    protected List<DetalhesFormaPagamentoMultiplosCartoesWS> detalhesFormaPagamentoMultiplosCartoes;
    protected long numeroTransacao;
    protected int statusTransacao;

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
     * Gets the value of the detalhesFormaPagamentoMultiplosCartoes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the detalhesFormaPagamentoMultiplosCartoes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDetalhesFormaPagamentoMultiplosCartoes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DetalhesFormaPagamentoMultiplosCartoesWS }
     * 
     * 
     */
    public List<DetalhesFormaPagamentoMultiplosCartoesWS> getDetalhesFormaPagamentoMultiplosCartoes() {
        if (detalhesFormaPagamentoMultiplosCartoes == null) {
            detalhesFormaPagamentoMultiplosCartoes = new ArrayList<DetalhesFormaPagamentoMultiplosCartoesWS>();
        }
        return this.detalhesFormaPagamentoMultiplosCartoes;
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

}
