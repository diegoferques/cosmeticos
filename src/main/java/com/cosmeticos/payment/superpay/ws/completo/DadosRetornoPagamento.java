//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.15 às 08:25:29 AM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de dadosRetornoPagamento complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="dadosRetornoPagamento"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="autorizacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoFormaPagamento" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="codigoTransacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataAprovacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="erro" type="{http://pagamentos.webservices.superpay.ernet.com.br/}erroRest" minOccurs="0"/&gt;
 *         &lt;element name="meioPagamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mensagemVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="multiploCartao" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="nsu" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroComprovanteVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="oneClick" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="parcelas" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="statusTransacao" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="urlPagamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="valorDesconto" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dadosRetornoPagamento", propOrder = {
    "autorizacao",
    "codigoEstabelecimento",
    "codigoFormaPagamento",
    "codigoTransacaoOperadora",
    "dataAprovacaoOperadora",
    "erro",
    "meioPagamento",
    "mensagemVenda",
    "multiploCartao",
    "nsu",
    "numeroComprovanteVenda",
    "numeroTransacao",
    "oneClick",
    "parcelas",
    "statusTransacao",
    "token",
    "urlPagamento",
    "valor",
    "valorDesconto"
})
public class DadosRetornoPagamento {

    protected String autorizacao;
    protected String codigoEstabelecimento;
    protected Long codigoFormaPagamento;
    protected String codigoTransacaoOperadora;
    protected String dataAprovacaoOperadora;
    protected ErroRest erro;
    protected String meioPagamento;
    protected String mensagemVenda;
    protected Integer multiploCartao;
    protected String nsu;
    protected String numeroComprovanteVenda;
    protected Long numeroTransacao;
    protected Integer oneClick;
    protected Integer parcelas;
    protected Long statusTransacao;
    protected String token;
    protected String urlPagamento;
    protected Long valor;
    protected Long valorDesconto;

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
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCodigoFormaPagamento() {
        return codigoFormaPagamento;
    }

    /**
     * Define o valor da propriedade codigoFormaPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCodigoFormaPagamento(Long value) {
        this.codigoFormaPagamento = value;
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
     * Obtém o valor da propriedade erro.
     * 
     * @return
     *     possible object is
     *     {@link ErroRest }
     *     
     */
    public ErroRest getErro() {
        return erro;
    }

    /**
     * Define o valor da propriedade erro.
     * 
     * @param value
     *     allowed object is
     *     {@link ErroRest }
     *     
     */
    public void setErro(ErroRest value) {
        this.erro = value;
    }

    /**
     * Obtém o valor da propriedade meioPagamento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeioPagamento() {
        return meioPagamento;
    }

    /**
     * Define o valor da propriedade meioPagamento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeioPagamento(String value) {
        this.meioPagamento = value;
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
     * Obtém o valor da propriedade multiploCartao.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMultiploCartao() {
        return multiploCartao;
    }

    /**
     * Define o valor da propriedade multiploCartao.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMultiploCartao(Integer value) {
        this.multiploCartao = value;
    }

    /**
     * Obtém o valor da propriedade nsu.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNsu() {
        return nsu;
    }

    /**
     * Define o valor da propriedade nsu.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNsu(String value) {
        this.nsu = value;
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
     * Obtém o valor da propriedade oneClick.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOneClick() {
        return oneClick;
    }

    /**
     * Define o valor da propriedade oneClick.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOneClick(Integer value) {
        this.oneClick = value;
    }

    /**
     * Obtém o valor da propriedade parcelas.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getParcelas() {
        return parcelas;
    }

    /**
     * Define o valor da propriedade parcelas.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setParcelas(Integer value) {
        this.parcelas = value;
    }

    /**
     * Obtém o valor da propriedade statusTransacao.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getStatusTransacao() {
        return statusTransacao;
    }

    /**
     * Define o valor da propriedade statusTransacao.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setStatusTransacao(Long value) {
        this.statusTransacao = value;
    }

    /**
     * Obtém o valor da propriedade token.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Define o valor da propriedade token.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
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
     * Obtém o valor da propriedade valor.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getValor() {
        return valor;
    }

    /**
     * Define o valor da propriedade valor.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setValor(Long value) {
        this.valor = value;
    }

    /**
     * Obtém o valor da propriedade valorDesconto.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getValorDesconto() {
        return valorDesconto;
    }

    /**
     * Define o valor da propriedade valorDesconto.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setValorDesconto(Long value) {
        this.valorDesconto = value;
    }

}
