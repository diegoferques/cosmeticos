package com.cosmeticos.cielo.model;




import com.cosmeticos.cielo.model.CieloCreditCard;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RequestCieloPayment {

    /**
     * Texto 	Obrigatorio 	Tipo do Meio de Pagamento.
     */
    public String type;

    /**
     * Número 	Obrigatorio 	Valor do Pedido (ser enviado em centavos).
     */
    public Integer amount;

    /**
     * Texto 	Opcional 	Moeda na qual o pagamento será feito (BRL).
     */
    public String currency;

    /**
     * Texto 	Opcional 	Pais na qual o pagamento será feito.
     */
    public String country;
    public Integer serviceTaxAmount;

    /**
     * Número 	Obrigatorio 	Número de Parcelas.
     */
    public Integer installments;

    /**
     * Texto 	Opcional 	Tipo de parcelamento - Loja (ByMerchant) ou Cartão (ByIssuer).
     */
    public String interest;

    /**
     * Booleano — 	Opcional (Default false) 	Booleano que identifica que a autorização deve ser com captura automática.
     */
    public Boolean capture;

    /**
     * Booleano 	— 	Opcional (Default false) 	Define se o comprador será direcionado ao Banco emissor para autenticação do cartão
     */
    public Boolean authenticate;

    public String softDescriptor;

    public CieloCreditCard creditCard;

}
