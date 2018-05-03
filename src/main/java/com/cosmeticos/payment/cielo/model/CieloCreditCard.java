
package com.cosmeticos.payment.cielo.model;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CieloCreditCard {

    /**
     * Texto 	Obrigatorio 	Número do Cartão do Comprador.
     */
    public String cardNumber;

    /**
     * Texto 	Opcional 	Nome do Comprador impresso no cartão.
     */
    public String holder;

    /**
     *  Texto 	Obrigatorio 	Data de validade impresso no cartão.
     */
    public String expirationDate;

    /**
     * Texto 	Opcional 	Código de segurança impresso no verso do cartão - Ver Anexo.
     */
    public String securityCode;

    /**
     * Booleano 	— 	Opcional (Default false) 	Booleano que identifica se o cartão será salvo para gerar o CardToken.
     */
    public Boolean saveCard;

    /**
     * Texto 	Obrigatorio 	Bandeira do cartão (Visa / Master / Amex / Elo / Aura / JCB / Diners / Discover / Hipercard).
     */
    public String brand;

    /**
     * Campo d eResponse. Token de identificação do Cartão. 	Guid 	36 	xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     */
    public String cardToken;
}
