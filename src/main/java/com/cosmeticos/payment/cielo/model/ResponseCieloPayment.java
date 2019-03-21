
package com.cosmeticos.payment.cielo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResponseCieloPayment {

    private Integer serviceTaxAmount;
    private Integer installments;
    private String interest;
    private Boolean capture;
    private Boolean authenticate;
    private CieloCreditCard creditCard;

    /**
     * Número da autorização, identico ao NSU. 	Texto 	6 	Texto alfanumérico
     */
    private String proofOfSale;

    /**
     * Id da transação na adquirente. 	Texto 	20 	Texto alfanumérico
     */
    private String tid;

    /**
     * Código de autorização. 	Texto 	6 	Texto alfanumérico
     */
    private String authorizationCode;

    /**
     * Texto que será impresso na fatura bancaria do portador - Disponivel apenas para VISA/MASTER - nao permite caracteres especiais 	Texto 	13 	Texto alfanumérico
     */
    private String softDescriptor;

    /**
     * Campo Identificador do Pedido. 	Guid 	36 	xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     */
    private String paymentId;
    private String type;
    private Integer amount;
    private Integer capturedAmount;
    private String country;
    private List<Object> extraDataCollection = null;

    /**
     * OrderStatus da Transação. 	Byte 	— 	2
     */
    @JsonProperty("OrderStatus")
    private Integer status;

    /**
     * Código de retorno da Adquirência. 	Texto 	32 	Texto alfanumérico
     */
    private String returnCode;

    /**
     *  	Mensagem de retorno da Adquirência. 	Texto 	512 	Texto alfanumérico
     */
    private String returnMessage;
    private List<ResponseLink> links = null;

    public ResponseCieloPayment() {
    }
}
