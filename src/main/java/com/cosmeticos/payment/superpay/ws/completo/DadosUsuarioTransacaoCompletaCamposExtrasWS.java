//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 01:45:10 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.completo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de dadosUsuarioTransacaoCompletaCamposExtrasWS complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="dadosUsuarioTransacaoCompletaCamposExtrasWS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipoCliente" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="nomeComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="emailComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="documentoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="documento2Comprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sexoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataNascimentoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="telefoneComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dddComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ddiComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoTipoTelefoneComprador" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="telefoneAdicionalComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dddAdicionalComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ddiAdicionalComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoTipoTelefoneAdicionalComprador" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="paisComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="enderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroEnderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bairroEnderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="complementoEnderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cidadeEnderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estadoEnderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cepEnderecoComprador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nomeEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="telefoneEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dddEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ddiEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoTipoTelefoneEntrega" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="telefoneAdicionalEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dddAdicionalEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ddiAdicionalEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codigoTipoTelefoneAdicionalEntrega" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="paisEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="enderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroEnderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bairroEnderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="complementoEnderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cidadeEnderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estadoEnderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cepEnderecoEntrega" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dadosUsuarioTransacaoCompletaCamposExtrasWS", propOrder = {
    "codigoCliente",
    "tipoCliente",
    "nomeComprador",
    "emailComprador",
    "documentoComprador",
    "documento2Comprador",
    "sexoComprador",
    "dataNascimentoComprador",
    "telefoneComprador",
    "dddComprador",
    "ddiComprador",
    "codigoTipoTelefoneComprador",
    "telefoneAdicionalComprador",
    "dddAdicionalComprador",
    "ddiAdicionalComprador",
    "codigoTipoTelefoneAdicionalComprador",
    "paisComprador",
    "enderecoComprador",
    "numeroEnderecoComprador",
    "bairroEnderecoComprador",
    "complementoEnderecoComprador",
    "cidadeEnderecoComprador",
    "estadoEnderecoComprador",
    "cepEnderecoComprador",
    "nomeEntrega",
    "telefoneEntrega",
    "dddEntrega",
    "ddiEntrega",
    "codigoTipoTelefoneEntrega",
    "telefoneAdicionalEntrega",
    "dddAdicionalEntrega",
    "ddiAdicionalEntrega",
    "codigoTipoTelefoneAdicionalEntrega",
    "paisEntrega",
    "enderecoEntrega",
    "numeroEnderecoEntrega",
    "bairroEnderecoEntrega",
    "complementoEnderecoEntrega",
    "cidadeEnderecoEntrega",
    "estadoEnderecoEntrega",
    "cepEnderecoEntrega"
})
public class DadosUsuarioTransacaoCompletaCamposExtrasWS {

    protected String codigoCliente;
    protected long tipoCliente;
    protected String nomeComprador;
    protected String emailComprador;
    protected String documentoComprador;
    protected String documento2Comprador;
    protected String sexoComprador;
    protected String dataNascimentoComprador;
    protected String telefoneComprador;
    protected String dddComprador;
    protected String ddiComprador;
    protected long codigoTipoTelefoneComprador;
    protected String telefoneAdicionalComprador;
    protected String dddAdicionalComprador;
    protected String ddiAdicionalComprador;
    protected long codigoTipoTelefoneAdicionalComprador;
    protected String paisComprador;
    protected String enderecoComprador;
    protected String numeroEnderecoComprador;
    protected String bairroEnderecoComprador;
    protected String complementoEnderecoComprador;
    protected String cidadeEnderecoComprador;
    protected String estadoEnderecoComprador;
    protected String cepEnderecoComprador;
    protected String nomeEntrega;
    protected String telefoneEntrega;
    protected String dddEntrega;
    protected String ddiEntrega;
    protected long codigoTipoTelefoneEntrega;
    protected String telefoneAdicionalEntrega;
    protected String dddAdicionalEntrega;
    protected String ddiAdicionalEntrega;
    protected long codigoTipoTelefoneAdicionalEntrega;
    protected String paisEntrega;
    protected String enderecoEntrega;
    protected String numeroEnderecoEntrega;
    protected String bairroEnderecoEntrega;
    protected String complementoEnderecoEntrega;
    protected String cidadeEnderecoEntrega;
    protected String estadoEnderecoEntrega;
    protected String cepEnderecoEntrega;

    /**
     * Obtém o valor da propriedade codigoCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCliente() {
        return codigoCliente;
    }

    /**
     * Define o valor da propriedade codigoCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCliente(String value) {
        this.codigoCliente = value;
    }

    /**
     * Obtém o valor da propriedade tipoCliente.
     * 
     */
    public long getTipoCliente() {
        return tipoCliente;
    }

    /**
     * Define o valor da propriedade tipoCliente.
     * 
     */
    public void setTipoCliente(long value) {
        this.tipoCliente = value;
    }

    /**
     * Obtém o valor da propriedade nomeComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeComprador() {
        return nomeComprador;
    }

    /**
     * Define o valor da propriedade nomeComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeComprador(String value) {
        this.nomeComprador = value;
    }

    /**
     * Obtém o valor da propriedade emailComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailComprador() {
        return emailComprador;
    }

    /**
     * Define o valor da propriedade emailComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailComprador(String value) {
        this.emailComprador = value;
    }

    /**
     * Obtém o valor da propriedade documentoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentoComprador() {
        return documentoComprador;
    }

    /**
     * Define o valor da propriedade documentoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentoComprador(String value) {
        this.documentoComprador = value;
    }

    /**
     * Obtém o valor da propriedade documento2Comprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumento2Comprador() {
        return documento2Comprador;
    }

    /**
     * Define o valor da propriedade documento2Comprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumento2Comprador(String value) {
        this.documento2Comprador = value;
    }

    /**
     * Obtém o valor da propriedade sexoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSexoComprador() {
        return sexoComprador;
    }

    /**
     * Define o valor da propriedade sexoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSexoComprador(String value) {
        this.sexoComprador = value;
    }

    /**
     * Obtém o valor da propriedade dataNascimentoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataNascimentoComprador() {
        return dataNascimentoComprador;
    }

    /**
     * Define o valor da propriedade dataNascimentoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataNascimentoComprador(String value) {
        this.dataNascimentoComprador = value;
    }

    /**
     * Obtém o valor da propriedade telefoneComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefoneComprador() {
        return telefoneComprador;
    }

    /**
     * Define o valor da propriedade telefoneComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefoneComprador(String value) {
        this.telefoneComprador = value;
    }

    /**
     * Obtém o valor da propriedade dddComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDddComprador() {
        return dddComprador;
    }

    /**
     * Define o valor da propriedade dddComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDddComprador(String value) {
        this.dddComprador = value;
    }

    /**
     * Obtém o valor da propriedade ddiComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDdiComprador() {
        return ddiComprador;
    }

    /**
     * Define o valor da propriedade ddiComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDdiComprador(String value) {
        this.ddiComprador = value;
    }

    /**
     * Obtém o valor da propriedade codigoTipoTelefoneComprador.
     * 
     */
    public long getCodigoTipoTelefoneComprador() {
        return codigoTipoTelefoneComprador;
    }

    /**
     * Define o valor da propriedade codigoTipoTelefoneComprador.
     * 
     */
    public void setCodigoTipoTelefoneComprador(long value) {
        this.codigoTipoTelefoneComprador = value;
    }

    /**
     * Obtém o valor da propriedade telefoneAdicionalComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefoneAdicionalComprador() {
        return telefoneAdicionalComprador;
    }

    /**
     * Define o valor da propriedade telefoneAdicionalComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefoneAdicionalComprador(String value) {
        this.telefoneAdicionalComprador = value;
    }

    /**
     * Obtém o valor da propriedade dddAdicionalComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDddAdicionalComprador() {
        return dddAdicionalComprador;
    }

    /**
     * Define o valor da propriedade dddAdicionalComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDddAdicionalComprador(String value) {
        this.dddAdicionalComprador = value;
    }

    /**
     * Obtém o valor da propriedade ddiAdicionalComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDdiAdicionalComprador() {
        return ddiAdicionalComprador;
    }

    /**
     * Define o valor da propriedade ddiAdicionalComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDdiAdicionalComprador(String value) {
        this.ddiAdicionalComprador = value;
    }

    /**
     * Obtém o valor da propriedade codigoTipoTelefoneAdicionalComprador.
     * 
     */
    public long getCodigoTipoTelefoneAdicionalComprador() {
        return codigoTipoTelefoneAdicionalComprador;
    }

    /**
     * Define o valor da propriedade codigoTipoTelefoneAdicionalComprador.
     * 
     */
    public void setCodigoTipoTelefoneAdicionalComprador(long value) {
        this.codigoTipoTelefoneAdicionalComprador = value;
    }

    /**
     * Obtém o valor da propriedade paisComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisComprador() {
        return paisComprador;
    }

    /**
     * Define o valor da propriedade paisComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisComprador(String value) {
        this.paisComprador = value;
    }

    /**
     * Obtém o valor da propriedade enderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnderecoComprador() {
        return enderecoComprador;
    }

    /**
     * Define o valor da propriedade enderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnderecoComprador(String value) {
        this.enderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade numeroEnderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroEnderecoComprador() {
        return numeroEnderecoComprador;
    }

    /**
     * Define o valor da propriedade numeroEnderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroEnderecoComprador(String value) {
        this.numeroEnderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade bairroEnderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBairroEnderecoComprador() {
        return bairroEnderecoComprador;
    }

    /**
     * Define o valor da propriedade bairroEnderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBairroEnderecoComprador(String value) {
        this.bairroEnderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade complementoEnderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplementoEnderecoComprador() {
        return complementoEnderecoComprador;
    }

    /**
     * Define o valor da propriedade complementoEnderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplementoEnderecoComprador(String value) {
        this.complementoEnderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade cidadeEnderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCidadeEnderecoComprador() {
        return cidadeEnderecoComprador;
    }

    /**
     * Define o valor da propriedade cidadeEnderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCidadeEnderecoComprador(String value) {
        this.cidadeEnderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade estadoEnderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoEnderecoComprador() {
        return estadoEnderecoComprador;
    }

    /**
     * Define o valor da propriedade estadoEnderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoEnderecoComprador(String value) {
        this.estadoEnderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade cepEnderecoComprador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCepEnderecoComprador() {
        return cepEnderecoComprador;
    }

    /**
     * Define o valor da propriedade cepEnderecoComprador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCepEnderecoComprador(String value) {
        this.cepEnderecoComprador = value;
    }

    /**
     * Obtém o valor da propriedade nomeEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomeEntrega() {
        return nomeEntrega;
    }

    /**
     * Define o valor da propriedade nomeEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomeEntrega(String value) {
        this.nomeEntrega = value;
    }

    /**
     * Obtém o valor da propriedade telefoneEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefoneEntrega() {
        return telefoneEntrega;
    }

    /**
     * Define o valor da propriedade telefoneEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefoneEntrega(String value) {
        this.telefoneEntrega = value;
    }

    /**
     * Obtém o valor da propriedade dddEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDddEntrega() {
        return dddEntrega;
    }

    /**
     * Define o valor da propriedade dddEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDddEntrega(String value) {
        this.dddEntrega = value;
    }

    /**
     * Obtém o valor da propriedade ddiEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDdiEntrega() {
        return ddiEntrega;
    }

    /**
     * Define o valor da propriedade ddiEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDdiEntrega(String value) {
        this.ddiEntrega = value;
    }

    /**
     * Obtém o valor da propriedade codigoTipoTelefoneEntrega.
     * 
     */
    public long getCodigoTipoTelefoneEntrega() {
        return codigoTipoTelefoneEntrega;
    }

    /**
     * Define o valor da propriedade codigoTipoTelefoneEntrega.
     * 
     */
    public void setCodigoTipoTelefoneEntrega(long value) {
        this.codigoTipoTelefoneEntrega = value;
    }

    /**
     * Obtém o valor da propriedade telefoneAdicionalEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefoneAdicionalEntrega() {
        return telefoneAdicionalEntrega;
    }

    /**
     * Define o valor da propriedade telefoneAdicionalEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefoneAdicionalEntrega(String value) {
        this.telefoneAdicionalEntrega = value;
    }

    /**
     * Obtém o valor da propriedade dddAdicionalEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDddAdicionalEntrega() {
        return dddAdicionalEntrega;
    }

    /**
     * Define o valor da propriedade dddAdicionalEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDddAdicionalEntrega(String value) {
        this.dddAdicionalEntrega = value;
    }

    /**
     * Obtém o valor da propriedade ddiAdicionalEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDdiAdicionalEntrega() {
        return ddiAdicionalEntrega;
    }

    /**
     * Define o valor da propriedade ddiAdicionalEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDdiAdicionalEntrega(String value) {
        this.ddiAdicionalEntrega = value;
    }

    /**
     * Obtém o valor da propriedade codigoTipoTelefoneAdicionalEntrega.
     * 
     */
    public long getCodigoTipoTelefoneAdicionalEntrega() {
        return codigoTipoTelefoneAdicionalEntrega;
    }

    /**
     * Define o valor da propriedade codigoTipoTelefoneAdicionalEntrega.
     * 
     */
    public void setCodigoTipoTelefoneAdicionalEntrega(long value) {
        this.codigoTipoTelefoneAdicionalEntrega = value;
    }

    /**
     * Obtém o valor da propriedade paisEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisEntrega() {
        return paisEntrega;
    }

    /**
     * Define o valor da propriedade paisEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisEntrega(String value) {
        this.paisEntrega = value;
    }

    /**
     * Obtém o valor da propriedade enderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    /**
     * Define o valor da propriedade enderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnderecoEntrega(String value) {
        this.enderecoEntrega = value;
    }

    /**
     * Obtém o valor da propriedade numeroEnderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroEnderecoEntrega() {
        return numeroEnderecoEntrega;
    }

    /**
     * Define o valor da propriedade numeroEnderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroEnderecoEntrega(String value) {
        this.numeroEnderecoEntrega = value;
    }

    /**
     * Obtém o valor da propriedade bairroEnderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBairroEnderecoEntrega() {
        return bairroEnderecoEntrega;
    }

    /**
     * Define o valor da propriedade bairroEnderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBairroEnderecoEntrega(String value) {
        this.bairroEnderecoEntrega = value;
    }

    /**
     * Obtém o valor da propriedade complementoEnderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplementoEnderecoEntrega() {
        return complementoEnderecoEntrega;
    }

    /**
     * Define o valor da propriedade complementoEnderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplementoEnderecoEntrega(String value) {
        this.complementoEnderecoEntrega = value;
    }

    /**
     * Obtém o valor da propriedade cidadeEnderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCidadeEnderecoEntrega() {
        return cidadeEnderecoEntrega;
    }

    /**
     * Define o valor da propriedade cidadeEnderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCidadeEnderecoEntrega(String value) {
        this.cidadeEnderecoEntrega = value;
    }

    /**
     * Obtém o valor da propriedade estadoEnderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoEnderecoEntrega() {
        return estadoEnderecoEntrega;
    }

    /**
     * Define o valor da propriedade estadoEnderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoEnderecoEntrega(String value) {
        this.estadoEnderecoEntrega = value;
    }

    /**
     * Obtém o valor da propriedade cepEnderecoEntrega.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCepEnderecoEntrega() {
        return cepEnderecoEntrega;
    }

    /**
     * Define o valor da propriedade cepEnderecoEntrega.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCepEnderecoEntrega(String value) {
        this.cepEnderecoEntrega = value;
    }

}
