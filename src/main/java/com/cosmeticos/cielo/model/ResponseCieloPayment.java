
package com.cosmeticos.cielo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseCieloPayment {

    public Integer serviceTaxAmount;
    public Integer installments;
    public String interest;
    public Boolean capture;
    public Boolean authenticate;
    public CieloCreditCard creditCard;

    /**
     * Número da autorização, identico ao NSU. 	Texto 	6 	Texto alfanumérico
     */
    public String proofOfSale;

    /**
     * Id da transação na adquirente. 	Texto 	20 	Texto alfanumérico
     */
    public String tid;

    /**
     * Código de autorização. 	Texto 	6 	Texto alfanumérico
     */
    public String authorizationCode;

    /**
     * Texto que será impresso na fatura bancaria do portador - Disponivel apenas para VISA/MASTER - nao permite caracteres especiais 	Texto 	13 	Texto alfanumérico
     */
    public String softDescriptor;

    /**
     * Campo Identificador do Pedido. 	Guid 	36 	xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     */
    public String paymentId;
    public String type;
    public Integer amount;
    public Integer capturedAmount;
    public String country;
    public List<Object> extraDataCollection = null;

    /**
     * Status da Transação. 	Byte 	— 	2
     */
    @JsonProperty("Status")
    public Integer status;

    /**
     * Código de retorno da Adquirência. 	Texto 	32 	Texto alfanumérico
     */
    public String returnCode;

    /**
     *  	Mensagem de retorno da Adquirência. 	Texto 	512 	Texto alfanumérico
     */
    public String returnMessage;
    public List<ResponseLink> links = null;

    public ResponseCieloPayment() {
    }
}
