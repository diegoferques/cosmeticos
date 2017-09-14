//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.11 às 01:09:11 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de resultadoConsultaTransacaoWSAVS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="resultadoConsultaTransacaoWSAVS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="autorizacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="cep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoFormaPagamento" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="codigoTransacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataAprovacaoOperadora" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="data_Expi" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="endereco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mensagemVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="msgAvs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numCartao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroComprovanteVenda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroTransacao" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="origem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="parcelas" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="respAVS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="statusTransacao" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="taxaEmbarque" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="urlPagamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="valor" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="valorDesconto" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultadoConsultaTransacaoWSAVS", propOrder = {
    "autorizacao",
    "cep",
    "codigoEstabelecimento",
    "codigoFormaPagamento",
    "codigoTransacaoOperadora",
    "complemento",
    "cpf",
    "dataAprovacaoOperadora",
    "dataExpi",
    "endereco",
    "mensagemVenda",
    "msgAvs",
    "numCartao",
    "numero",
    "numeroComprovanteVenda",
    "numeroTransacao",
    "origem",
    "parcelas",
    "respAVS",
    "statusTransacao",
    "taxaEmbarque",
    "urlPagamento",
    "valor",
    "valorDesconto"
})
public class ResultadoConsultaTransacaoWSAVS {

    protected int autorizacao;
    protected String cep;
    protected String codigoEstabelecimento;
    protected int codigoFormaPagamento;
    protected int codigoTransacaoOperadora;
    protected String complemento;
    protected String cpf;
    protected String dataAprovacaoOperadora;
    @XmlElement(name = "data_Expi")
    protected String dataExpi;
    protected String endereco;
    protected String mensagemVenda;
    protected String msgAvs;
    protected String numCartao;
    protected String numero;
    protected String numeroComprovanteVenda;
    protected long numeroTransacao;
    protected String origem;
    protected int parcelas;
    protected String respAVS;
    protected int statusTransacao;
    protected long taxaEmbarque;
    protected String urlPagamento;
    protected long valor;
    protected long valorDesconto;

    /**
     * Obtém o valor da propriedade autorizacao.
     * 
     */
    public int getAutorizacao() {
        return autorizacao;
    }

    /**
     * Define o valor da propriedade autorizacao.
     * 
     */
    public void setAutorizacao(int value) {
        this.autorizacao = value;
    }

    /**
     * Obtém o valor da propriedade cep.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCep() {
        return cep;
    }

    /**
     * Define o valor da propriedade cep.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCep(String value) {
        this.cep = value;
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
     * Obtém o valor da propriedade codigoTransacaoOperadora.
     * 
     */
    public int getCodigoTransacaoOperadora() {
        return codigoTransacaoOperadora;
    }

    /**
     * Define o valor da propriedade codigoTransacaoOperadora.
     * 
     */
    public void setCodigoTransacaoOperadora(int value) {
        this.codigoTransacaoOperadora = value;
    }

    /**
     * Obtém o valor da propriedade complemento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * Define o valor da propriedade complemento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplemento(String value) {
        this.complemento = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpf(String value) {
        this.cpf = value;
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
     * Obtém o valor da propriedade dataExpi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataExpi() {
        return dataExpi;
    }

    /**
     * Define o valor da propriedade dataExpi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataExpi(String value) {
        this.dataExpi = value;
    }

    /**
     * Obtém o valor da propriedade endereco.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Define o valor da propriedade endereco.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndereco(String value) {
        this.endereco = value;
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
     * Obtém o valor da propriedade msgAvs.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgAvs() {
        return msgAvs;
    }

    /**
     * Define o valor da propriedade msgAvs.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgAvs(String value) {
        this.msgAvs = value;
    }

    /**
     * Obtém o valor da propriedade numCartao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumCartao() {
        return numCartao;
    }

    /**
     * Define o valor da propriedade numCartao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumCartao(String value) {
        this.numCartao = value;
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
     * Obtém o valor da propriedade origem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrigem() {
        return origem;
    }

    /**
     * Define o valor da propriedade origem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrigem(String value) {
        this.origem = value;
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
     * Obtém o valor da propriedade respAVS.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRespAVS() {
        return respAVS;
    }

    /**
     * Define o valor da propriedade respAVS.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRespAVS(String value) {
        this.respAVS = value;
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

}
