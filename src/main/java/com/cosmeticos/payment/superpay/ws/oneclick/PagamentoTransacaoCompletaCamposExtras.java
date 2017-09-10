//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.09.10 às 01:01:37 PM BRT 
//


package com.cosmeticos.payment.superpay.ws.oneclick;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de pagamentoTransacaoCompletaCamposExtras complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="pagamentoTransacaoCompletaCamposExtras"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transacao" type="{http://pagamentos.webservices.superpay.ernet.com.br/}transacaoCompletaCamposExtrasWS" minOccurs="0"/&gt;
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
@XmlType(name = "pagamentoTransacaoCompletaCamposExtras", propOrder = {
    "transacao",
    "usuario",
    "senha"
})
public class PagamentoTransacaoCompletaCamposExtras {

    protected TransacaoCompletaCamposExtrasWS transacao;
    protected String usuario;
    protected String senha;

    /**
     * Obtém o valor da propriedade transacao.
     * 
     * @return
     *     possible object is
     *     {@link TransacaoCompletaCamposExtrasWS }
     *     
     */
    public TransacaoCompletaCamposExtrasWS getTransacao() {
        return transacao;
    }

    /**
     * Define o valor da propriedade transacao.
     * 
     * @param value
     *     allowed object is
     *     {@link TransacaoCompletaCamposExtrasWS }
     *     
     */
    public void setTransacao(TransacaoCompletaCamposExtrasWS value) {
        this.transacao = value;
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
