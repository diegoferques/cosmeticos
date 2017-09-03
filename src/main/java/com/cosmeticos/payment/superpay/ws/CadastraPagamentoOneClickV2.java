//
// Este arquivo foi gerado pela Arquitetura JavaTM para Implementação de Referência (JAXB) de Bind XML, v2.2.11 
// Consulte <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
// Gerado em: 2017.08.31 às 03:17:48 PM BRT 
//


package com.cosmeticos.payment.superpay.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de cadastraPagamentoOneClickV2 complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>
 * &lt;complexType name="cadastraPagamentoOneClickV2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dadosOneClick" type="{http://pagamentos.webservices.superpay.ernet.com.br/}dadosCadastroPagamentoOneClickWSV2" minOccurs="0"/&gt;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cadastraPagamentoOneClickV2", propOrder = {
    "dadosOneClick",
    "usuario",
    "senha"
})
public class CadastraPagamentoOneClickV2 {

    protected DadosCadastroPagamentoOneClickWSV2 dadosOneClick;
    protected String usuario;
    protected String senha;

    /**
     * Obtém o valor da propriedade dadosOneClick.
     * 
     * @return
     *     possible object is
     *     {@link DadosCadastroPagamentoOneClickWSV2 }
     *     
     */
    public DadosCadastroPagamentoOneClickWSV2 getDadosOneClick() {
        return dadosOneClick;
    }

    /**
     * Define o valor da propriedade dadosOneClick.
     * 
     * @param value
     *     allowed object is
     *     {@link DadosCadastroPagamentoOneClickWSV2 }
     *     
     */
    public void setDadosOneClick(DadosCadastroPagamentoOneClickWSV2 value) {
        this.dadosOneClick = value;
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
