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
 * <p>Classe Java de transacaoCompletaWSCartoesCredito complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="transacaoCompletaWSCartoesCredito"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="campoLivre1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dadosCartoesCredito" type="{http://pagamentos.webservices.superpay.ernet.com.br/}dadosCartoesCredito" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dadosUsuarioTransacao" type="{http://pagamentos.webservices.superpay.ernet.com.br/}dadosUsuarioTransacaoCompletaWS" minOccurs="0"/&gt;
 *         &lt;element name="IP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="itensDoPedido" type="{http://pagamentos.webservices.superpay.ernet.com.br/}itemPedidoTransacaoWS" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="origemTransacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="taxaEmbarque" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="urlCampainha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="urlRedirecionamentoNaoPago" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="urlRedirecionamentoPago" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="valorDesconto" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="vencimentoBoleto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transacaoCompletaWSCartoesCredito", propOrder = {
    "campoLivre1",
    "campoLivre2",
    "campoLivre3",
    "campoLivre4",
    "campoLivre5",
    "codigoEstabelecimento",
    "dadosCartoesCredito",
    "dadosUsuarioTransacao",
    "ip",
    "idioma",
    "itensDoPedido",
    "numeroTransacao",
    "origemTransacao",
    "taxaEmbarque",
    "urlCampainha",
    "urlRedirecionamentoNaoPago",
    "urlRedirecionamentoPago",
    "valorDesconto",
    "vencimentoBoleto"
})
public class TransacaoCompletaWSCartoesCredito {

    protected String campoLivre1;
    protected String campoLivre2;
    protected String campoLivre3;
    protected String campoLivre4;
    protected String campoLivre5;
    protected String codigoEstabelecimento;
    @XmlElement(nillable = true)
    protected List<DadosCartoesCredito> dadosCartoesCredito;
    protected DadosUsuarioTransacaoCompletaWS dadosUsuarioTransacao;
    @XmlElement(name = "IP")
    protected String ip;
    protected int idioma;
    @XmlElement(nillable = true)
    protected List<ItemPedidoTransacaoWS> itensDoPedido;
    protected long numeroTransacao;
    protected String origemTransacao;
    protected long taxaEmbarque;
    protected String urlCampainha;
    protected String urlRedirecionamentoNaoPago;
    protected String urlRedirecionamentoPago;
    protected long valorDesconto;
    protected String vencimentoBoleto;

    /**
     * Obtém o valor da propriedade campoLivre1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre1() {
        return campoLivre1;
    }

    /**
     * Define o valor da propriedade campoLivre1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre1(String value) {
        this.campoLivre1 = value;
    }

    /**
     * Obtém o valor da propriedade campoLivre2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre2() {
        return campoLivre2;
    }

    /**
     * Define o valor da propriedade campoLivre2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre2(String value) {
        this.campoLivre2 = value;
    }

    /**
     * Obtém o valor da propriedade campoLivre3.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre3() {
        return campoLivre3;
    }

    /**
     * Define o valor da propriedade campoLivre3.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre3(String value) {
        this.campoLivre3 = value;
    }

    /**
     * Obtém o valor da propriedade campoLivre4.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre4() {
        return campoLivre4;
    }

    /**
     * Define o valor da propriedade campoLivre4.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre4(String value) {
        this.campoLivre4 = value;
    }

    /**
     * Obtém o valor da propriedade campoLivre5.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCampoLivre5() {
        return campoLivre5;
    }

    /**
     * Define o valor da propriedade campoLivre5.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCampoLivre5(String value) {
        this.campoLivre5 = value;
    }

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
     * Gets the value of the dadosCartoesCredito property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dadosCartoesCredito property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDadosCartoesCredito().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DadosCartoesCredito }
     * 
     * 
     */
    public List<DadosCartoesCredito> getDadosCartoesCredito() {
        if (dadosCartoesCredito == null) {
            dadosCartoesCredito = new ArrayList<DadosCartoesCredito>();
        }
        return this.dadosCartoesCredito;
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
     * Obtém o valor da propriedade idioma.
     * 
     */
    public int getIdioma() {
        return idioma;
    }

    /**
     * Define o valor da propriedade idioma.
     * 
     */
    public void setIdioma(int value) {
        this.idioma = value;
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

    /**
     * Obtém o valor da propriedade taxaEmbarque.
     * 
     */
    public long getTaxaEmbarque() {
        return taxaEmbarque;
    }

    /**
     * Define o valor da propriedade taxaEmbarque.
     * 
     */
    public void setTaxaEmbarque(long value) {
        this.taxaEmbarque = value;
    }

    /**
     * Obtém o valor da propriedade urlCampainha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlCampainha() {
        return urlCampainha;
    }

    /**
     * Define o valor da propriedade urlCampainha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlCampainha(String value) {
        this.urlCampainha = value;
    }

    /**
     * Obtém o valor da propriedade urlRedirecionamentoNaoPago.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlRedirecionamentoNaoPago() {
        return urlRedirecionamentoNaoPago;
    }

    /**
     * Define o valor da propriedade urlRedirecionamentoNaoPago.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlRedirecionamentoNaoPago(String value) {
        this.urlRedirecionamentoNaoPago = value;
    }

    /**
     * Obtém o valor da propriedade urlRedirecionamentoPago.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlRedirecionamentoPago() {
        return urlRedirecionamentoPago;
    }

    /**
     * Define o valor da propriedade urlRedirecionamentoPago.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlRedirecionamentoPago(String value) {
        this.urlRedirecionamentoPago = value;
    }

    /**
     * Obtém o valor da propriedade valorDesconto.
     * 
     */
    public long getValorDesconto() {
        return valorDesconto;
    }

    /**
     * Define o valor da propriedade valorDesconto.
     * 
     */
    public void setValorDesconto(long value) {
        this.valorDesconto = value;
    }

    /**
     * Obtém o valor da propriedade vencimentoBoleto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVencimentoBoleto() {
        return vencimentoBoleto;
    }

    /**
     * Define o valor da propriedade vencimentoBoleto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVencimentoBoleto(String value) {
        this.vencimentoBoleto = value;
    }

}
