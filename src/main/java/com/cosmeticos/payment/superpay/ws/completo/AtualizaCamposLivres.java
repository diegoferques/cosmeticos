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
 * <p>Classe Java de atualizaCamposLivres complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="atualizaCamposLivres"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numeroPedido" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre4" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="campoLivre5" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="senha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "atualizaCamposLivres", propOrder = {
    "codigoEstabelecimento",
    "numeroPedido",
    "campoLivre1",
    "campoLivre2",
    "campoLivre3",
    "campoLivre4",
    "campoLivre5",
    "usuario",
    "senha"
})
public class AtualizaCamposLivres {

    protected String codigoEstabelecimento;
    protected Long numeroPedido;
    protected String campoLivre1;
    protected String campoLivre2;
    protected String campoLivre3;
    protected String campoLivre4;
    protected String campoLivre5;
    protected String usuario;
    protected String senha;

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
     * Obtém o valor da propriedade numeroPedido.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNumeroPedido() {
        return numeroPedido;
    }

    /**
     * Define o valor da propriedade numeroPedido.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNumeroPedido(Long value) {
        this.numeroPedido = value;
    }

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
     * Obtém o valor da propriedade usuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenha(String value) {
        this.senha = value;
    }

}
