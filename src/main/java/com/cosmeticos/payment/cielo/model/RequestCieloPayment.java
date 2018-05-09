package com.cosmeticos.payment.cielo.model;




import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestCieloPayment {

    /**
     * Texto 	Obrigatorio 	Tipo do Meio de Pagamento.
     */
    private String type;

    /**
     * Número 	Obrigatorio 	Valor do Pedido (ser enviado em centavos).
     */
    private Integer amount;

    /**
     * Texto 	Opcional 	Moeda na qual o pagamento será feito (BRL).
     */
    private String currency;

    /**
     * Texto 	Opcional 	Pais na qual o pagamento será feito.
     */
    private String country;

    private Integer serviceTaxAmount;

    /**
     * Número 	Obrigatorio 	Número de Parcelas.
     */
    private Integer installments;

    /**
     * Texto 	Opcional 	Tipo de parcelamento - Loja (ByMerchant) ou Cartão (ByIssuer).
     */
    private String interest;

    /**
     * Booleano — 	Opcional (Default false) 	Booleano que identifica que a autorização deve ser com captura automática.
     */
    private Boolean capture;

    /**
     * Booleano 	— 	Opcional (Default false) 	Define se o comprador será direcionado ao Banco emissor para autenticação do cartão
     */
    private Boolean authenticate;

    private String softDescriptor;

    private CieloCreditCard creditCard;

}
