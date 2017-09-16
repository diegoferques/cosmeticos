//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.16 às 12:23:47 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de atualizacaoTransacaoCompletaWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="atualizacaoTransacaoCompletaWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dadosUsuarioTransacao" type="{http://pagamentos.webservices.superpay.ernet.com.br/}dadosUsuarioTransacaoCompletaWS" minOccurs="0"/&gt;
 *         &lt;element name="IP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="itensDoPedido" type="{http://pagamentos.webservices.superpay.ernet.com.br/}itemPedidoTransacaoWS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="origemTransacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "atualizacaoTransacaoCompletaWS", propOrder = {
    "codigoEstabelecimento",
    "dadosUsuarioTransacao",
    "ip",
    "itensDoPedido",
    "numeroTransacao",
    "origemTransacao"
})
public class AtualizacaoTransacaoCompletaWS {

    protected String codigoEstabelecimento;
    protected DadosUsuarioTransacaoCompletaWS dadosUsuarioTransacao;
    @XmlElement(name = "IP")
    protected String ip;
    @XmlElement(nillable = true)
    protected List<ItemPedidoTransacaoWS> itensDoPedido;
    protected long numeroTransacao;
    protected String origemTransacao;

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
     * Obtém o valor da propriedade dadosUsuarioTransacao.
     * 
     * @return
     *     possible object is
     *     {@link DadosUsuarioTransacaoCompletaWS }
     *     
     */
    public DadosUsuarioTransacaoCompletaWS getDadosUsuarioTransacao() {
        return dadosUsuarioTransacao;
    }

    /**
     * Define o valor da propriedade dadosUsuarioTransacao.
     * 
     * @param value
     *     allowed object is
     *     {@link DadosUsuarioTransacaoCompletaWS }
     *     
     */
    public void setDadosUsuarioTransacao(DadosUsuarioTransacaoCompletaWS value) {
        this.dadosUsuarioTransacao = value;
    }

    /**
     * Obtém o valor da propriedade ip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIP() {
        return ip;
    }

    /**
     * Define o valor da propriedade ip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIP(String value) {
        this.ip = value;
    }

    /**
     * Gets the value of the itensDoPedido property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the itensDoPedido property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItensDoPedido().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemPedidoTransacaoWS }
     * 
     * 
     */
    public List<ItemPedidoTransacaoWS> getItensDoPedido() {
        if (itensDoPedido == null) {
            itensDoPedido = new ArrayList<ItemPedidoTransacaoWS>();
        }
        return this.itensDoPedido;
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
     * Obtém o valor da propriedade origemTransacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigemTransacao() {
        return origemTransacao;
    }

    /**
     * Define o valor da propriedade origemTransacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigemTransacao(String value) {
        this.origemTransacao = value;
    }

}
