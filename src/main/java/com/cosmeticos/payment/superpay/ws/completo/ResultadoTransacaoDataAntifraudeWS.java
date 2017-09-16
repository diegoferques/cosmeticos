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
 * <p>Classe Java de resultadoTransacaoDataAntifraudeWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="resultadoTransacaoDataAntifraudeWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoFormaPagamento" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="valorDesconto" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="taxaEmbarque" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="parcelas" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="urlPagamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="statusTransacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="autorizacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoTransacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataAprovacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataRetornoAntifraude" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroComprovanteVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mensagemVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cartoesUtilizados" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoTransacaoDataAntifraudeWS", propOrder = {
    "numeroTransacao",
    "codigoEstabelecimento",
    "codigoFormaPagamento",
    "valor",
    "valorDesconto",
    "taxaEmbarque",
    "parcelas",
    "urlPagamento",
    "statusTransacao",
    "autorizacao",
    "codigoTransacaoOperadora",
    "dataAprovacaoOperadora",
    "dataRetornoAntifraude",
    "numeroComprovanteVenda",
    "mensagemVenda",
    "cartoesUtilizados"
})
public class ResultadoTransacaoDataAntifraudeWS {

    protected long numeroTransacao;
    protected String codigoEstabelecimento;
    protected int codigoFormaPagamento;
    protected long valor;
    protected long valorDesconto;
    protected long taxaEmbarque;
    protected int parcelas;
    protected String urlPagamento;
    protected int statusTransacao;
    protected String autorizacao;
    protected String codigoTransacaoOperadora;
    protected String dataAprovacaoOperadora;
    protected String dataRetornoAntifraude;
    protected String numeroComprovanteVenda;
    protected String mensagemVenda;
    @XmlElement(nillable = true)
    protected List<String> cartoesUtilizados;

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
     * Obtém o valor da propriedade valor.
     * 
     */
    public long getValor() {
        return valor;
    }

    /**
     * Define o valor da propriedade valor.
     * 
     */
    public void setValor(long value) {
        this.valor = value;
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
     * Obtém o valor da propriedade urlPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUrlPagamento() {
        return urlPagamento;
    }

    /**
     * Define o valor da propriedade urlPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUrlPagamento(String value) {
        this.urlPagamento = value;
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

    /**
     * Obtém o valor da propriedade autorizacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAutorizacao() {
        return autorizacao;
    }

    /**
     * Define o valor da propriedade autorizacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAutorizacao(String value) {
        this.autorizacao = value;
    }

    /**
     * Obtém o valor da propriedade codigoTransacaoOperadora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoTransacaoOperadora() {
        return codigoTransacaoOperadora;
    }

    /**
     * Define o valor da propriedade codigoTransacaoOperadora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoTransacaoOperadora(String value) {
        this.codigoTransacaoOperadora = value;
    }

    /**
     * Obtém o valor da propriedade dataAprovacaoOperadora.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataAprovacaoOperadora() {
        return dataAprovacaoOperadora;
    }

    /**
     * Define o valor da propriedade dataAprovacaoOperadora.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataAprovacaoOperadora(String value) {
        this.dataAprovacaoOperadora = value;
    }

    /**
     * Obtém o valor da propriedade dataRetornoAntifraude.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataRetornoAntifraude() {
        return dataRetornoAntifraude;
    }

    /**
     * Define o valor da propriedade dataRetornoAntifraude.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataRetornoAntifraude(String value) {
        this.dataRetornoAntifraude = value;
    }

    /**
     * Obtém o valor da propriedade numeroComprovanteVenda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroComprovanteVenda() {
        return numeroComprovanteVenda;
    }

    /**
     * Define o valor da propriedade numeroComprovanteVenda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroComprovanteVenda(String value) {
        this.numeroComprovanteVenda = value;
    }

    /**
     * Obtém o valor da propriedade mensagemVenda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensagemVenda() {
        return mensagemVenda;
    }

    /**
     * Define o valor da propriedade mensagemVenda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensagemVenda(String value) {
        this.mensagemVenda = value;
    }

    /**
     * Gets the value of the cartoesUtilizados property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cartoesUtilizados property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCartoesUtilizados().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCartoesUtilizados() {
        if (cartoesUtilizados == null) {
            cartoesUtilizados = new ArrayList<String>();
        }
        return this.cartoesUtilizados;
    }

}
