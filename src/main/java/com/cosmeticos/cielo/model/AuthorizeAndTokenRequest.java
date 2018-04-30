
package com.cosmeticos.cielo.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthorizeAndTokenRequest {

    /**
     * Texto 	Obrigatorio 	Numero de identificação do Pedido a ser fornecido pelo iPretty (Order.idOrder).
     */
    public String merchantOrderId;
    public CieloCustomer customer;
    public RequestCieloPayment payment;

}
