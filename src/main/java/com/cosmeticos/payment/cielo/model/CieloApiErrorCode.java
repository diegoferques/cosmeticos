package com.cosmeticos.payment.cielo.model;

import com.cosmeticos.model.Payment;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Documentacao Cielo: https://developercielo.github.io/manual/cielo-ecommerce#c%C3%B3digos-de-erros-da-api
 */
public enum CieloApiErrorCode {
    TRANSACTION_ID_IS_REQUIRED(106, "Campo enviado está vazio ou invalido"),
    ORDERID_IS_INVALID_OR_DOES_NOT_EXISTS(107, "Campo enviado excede o tamanho ou contem caracteres especiais"),
    AMOUNT_MUST_BE_GREATER_OR_EQUAL_TO_ZERO(108, "Valor da transação deve ser maior que “0”"),
    PAYMENT_CURRENCY_IS_REQUIRED(109, "Campo enviado está vazio ou invalido"),
    INVALID_PAYMENT_CURRENCY(110, "Campo enviado está vazio ou invalido"),
    PAYMENT_COUNTRY_IS_REQUIRED(111, "Campo enviado está vazio ou invalido"),
    INVALID_PAYMENT_COUNTRY(112, "Campo enviado está vazio ou invalido"),
    INVALID_PAYMENT_CODE(113, "Campo enviado está vazio ou invalido"),
    THE_PROVIDED_MERCHANTID_IS_NOT_IN_CORRECT_FORMAT(114, "O MerchantId enviado não é um GUID"),
    THE_PROVIDED_MERCHANTID_WAS_NOT_FOUND(115, "O MerchantID não existe ou pertence a outro ambiente (EX: Sandbox)"),
    THE_PROVIDED_MERCHANTID_IS_BLOCKED(116, "Loja bloqueada, entre em contato com o suporte Cielo"),
    CREDIT_CARD_HOLDER_IS_REQUIRED(117, "Campo enviado está vazio ou invalido"),
    CREDIT_CARD_NUMBER_IS_REQUIRED(118, "Campo enviado está vazio ou invalido"),
    AT_LEAST_ONE_PAYMENT_IS_REQUIRED(119, "Nó “Payment” não enviado"),
    REQUEST_IP_NOT_ALLOWED_CHECK_YOUR_IP_WHITE_LIST(120, "IP bloqueado por questões de segurança"),
    CUSTOMER_IS_REQUIRED(121, "Nó “Customer” não enviado"),
    MERCHANTORDERID_IS_REQUIRED(122, "Campo enviado está vazio ou invalido"),
    INSTALLMENTS_MUST_BE_GREATER_OR_EQUAL_TO_ONE(123, "Numero de parcelas deve ser superior a 1"),
    CREDIT_CARD_IS_REQUIRED(124, "Campo enviado está vazio ou invalido"),
    CREDIT_CARD_EXPIRATION_DATE_IS_REQUIRED(125, "Campo enviado está vazio ou invalido"),
    CREDIT_CARD_EXPIRATION_DATE_IS_INVALID(126, "Campo enviado está vazio ou invalido"),
    YOU_MUST_PROVIDE_CREDITCARD_NUMBER(127, "Numero do cartão de crédito é obrigatório"),
    CARD_NUMBER_LENGTH_EXCEEDED(128, "Numero do cartão superiro a 16 digitos"),
    AFFILIATION_NOT_FOUND(129, "Meio de pagamento não vinculado a loja ou Provider invalido"),
    COULD_NOT_GET_CREDIT_CARD(130, "—"),
    MERCHANTKEY_IS_REQUIRED(131, "Campo enviado está vazio ou invalido"),
    MERCHANTKEY_IS_INVALID(132, "O Merchantkey enviado não é um válido"),
    PROVIDER_IS_NOT_SUPPORTED_FOR_THIS_PAYMENT_TYPE(133, "Provider enviado não existe"),
    FINGERPRINT_LENGTH_EXCEEDED(134, "Dado enviado excede o tamanho do campo"),
    MERCHANTDEFINEDFIELDVALUE_LENGTH_EXCEEDED(135, "Dado enviado excede o tamanho do campo"),
    ITEMDATANAME_LENGTH_EXCEEDED(136, "Dado enviado excede o tamanho do campo"),
    ITEMDATASKU_LENGTH_EXCEEDED(137, "Dado enviado excede o tamanho do campo"),
    PASSENGERDATANAME_LENGTH_EXCEEDED(138, "Dado enviado excede o tamanho do campo"),
    PASSENGERDATASTATUS_LENGTH_EXCEEDED(139, "Dado enviado excede o tamanho do campo"),
    PASSENGERDATAEMAIL_LENGTH_EXCEEDED(140, "Dado enviado excede o tamanho do campo"),
    PASSENGERDATAPHONE_LENGTH_EXCEEDED(141, "Dado enviado excede o tamanho do campo"),
    TRAVELDATAROUTE_LENGTH_EXCEEDED(142, "Dado enviado excede o tamanho do campo"),
    TRAVELDATAJOURNEYTYPE_LENGTH_EXCEEDED(143, "Dado enviado excede o tamanho do campo"),
    TRAVELLEGDATADESTINATION_LENGTH_EXCEEDED(144, "Dado enviado excede o tamanho do campo"),
    TRAVELLEGDATAORIGIN_LENGTH_EXCEEDED(145, "Dado enviado excede o tamanho do campo"),
    SECURITYCODE_LENGTH_EXCEEDED(146, "Dado enviado excede o tamanho do campo"),
    ADDRESS_STREET_LENGTH_EXCEEDED(147, "Dado enviado excede o tamanho do campo"),
    ADDRESS_NUMBER_LENGTH_EXCEEDED(148, "Dado enviado excede o tamanho do campo"),
    ADDRESS_COMPLEMENT_LENGTH_EXCEEDED(149, "Dado enviado excede o tamanho do campo"),
    ADDRESS_ZIPCODE_LENGTH_EXCEEDED(150, "Dado enviado excede o tamanho do campo"),
    ADDRESS_CITY_LENGTH_EXCEEDED(151, "Dado enviado excede o tamanho do campo"),
    ADDRESS_STATE_LENGTH_EXCEEDED(152, "Dado enviado excede o tamanho do campo"),
    ADDRESS_COUNTRY_LENGTH_EXCEEDED(153, "Dado enviado excede o tamanho do campo"),
    ADDRESS_DISTRICT_LENGTH_EXCEEDED(154, "Dado enviado excede o tamanho do campo"),
    CUSTOMER_NAME_LENGTH_EXCEEDED(155, "Dado enviado excede o tamanho do campo"),
    CUSTOMER_IDENTITY_LENGTH_EXCEEDED(156, "Dado enviado excede o tamanho do campo"),
    CUSTOMER_IDENTITYTYPE_LENGTH_EXCEEDED(157, "Dado enviado excede o tamanho do campo"),
    CUSTOMER_EMAIL_LENGTH_EXCEEDED(158, "Dado enviado excede o tamanho do campo"),
    EXTRADATA_NAME_LENGTH_EXCEEDED(159, "Dado enviado excede o tamanho do campo"),
    EXTRADATA_VALUE_LENGTH_EXCEEDED(160, "Dado enviado excede o tamanho do campo"),
    BOLETO_INSTRUCTIONS_LENGTH_EXCEEDED(161, "Dado enviado excede o tamanho do campo"),
    BOLETO_DEMOSTRATIVE_LENGTH_EXCEEDED(162, "Dado enviado excede o tamanho do campo"),
    RETURN_URL_IS_REQUIRED(163, "URL de retorno não é valida - Não é aceito paginação ou extenções (EX .PHP) na URL de retorno"),
    AUTHORIZENOW_IS_REQUIRED(166, "—"),
    ANTIFRAUD_NOT_CONFIGURED(167, "Antifraude não vinculado ao cadastro do lojista"),
    RECURRENT_PAYMENT_NOT_FOUND_168(168, "Recorrencia não encontrada"),
    RECURRENT_PAYMENT_IS_NOT_ACTIVE(169, "Recorrencia não está ativa. Execução paralizada"),
    CARTÃO_PROTEGIDO_NOT_CONFIGURED(170, "Cartão protegido não vinculado ao cadastro do lojista"),
    AFFILIATION_DATA_NOT_SENT(171, "Falha no processamento do pedido - Entre em contato com o suporte Cielo"),
    CREDENTIAL_CODE_IS_REQUIRED(172, "Falha na validação das credenciadas enviadas"),
    PAYMENT_METHOD_IS_NOT_ENABLED(173, "Meio de pagamento não vinculado ao cadastro do lojista"),
    CARD_NUMBER_IS_REQUIRED(174, "Campo enviado está vazio ou invalido"),
    EAN_IS_REQUIRED(175, "Campo enviado está vazio ou invalido"),
    PAYMENT_CURRENCY_IS_NOT_SUPPORTED(176, "Campo enviado está vazio ou invalido"),
    CARD_NUMBER_IS_INVALID(177, "Campo enviado está vazio ou invalido"),
    EAN_IS_INVALID(178, "Campo enviado está vazio ou invalido"),
    THE_MAX_NUMBER_OF_INSTALLMENTS_ALLOWED_FOR_RECURRING_PAYMENT_IS_1(179, "Campo enviado está vazio ou invalido"),
    THE_PROVIDED_CARD_PAYMENTTOKEN_WAS_NOT_FOUND(180, "Token do Cartão protegido não encontrado"),
    THE_MERCHANTIDJUSTCLICK_IS_NOT_CONFIGURED(181, "Token do Cartão protegido bloqueado"),
    BRAND_IS_REQUIRED(182, "Bandeira do cartão não enviado"),
    INVALID_CUSTOMER_BITHDATE(183, "Data de nascimento invalida ou futura"),
    REQUEST_COULD_NOT_BE_EMPTY(184, "Falha no formado ta requisição. Verifique o código enviado"),
    BRAND_IS_NOT_SUPPORTED_BY_SELECTED_PROVIDER(185, "Bandeira não suportada pela API Cielo"),
    THE_SELECTED_PROVIDER_DOES_NOT_SUPPORT_THE_OPTIONS_PROVIDED(186, "Meio de pagamento não suporta o comando enviado - The selected provider does not support the options provided (Capture, Authenticate, Recurrent or Installments)"),
    EXTRADATA_COLLECTION_CONTAINS_ONE_OR_MORE_DUPLICATED_NAMES(187, "—"),
    AVS_WITH_CPF_INVALID(188, "—"),
    AVS_WITH_LENGTH_OF_STREET_EXCEEDED(189, "Dado enviado excede o tamanho do campo"),
    AVS_WITH_LENGTH_OF_NUMBER_EXCEEDED(190, "Dado enviado excede o tamanho do campo"),
    AVS_WITH_LENGTH_OF_COMPLEMENT_EXCEEDED(190, "Dado enviado excede o tamanho do campo"),
    AVS_WITH_LENGTH_OF_DISTRICT_EXCEEDED(191, "Dado enviado excede o tamanho do campo"),
    AVS_WITH_ZIP_CODE_INVALID(192, "CEP enviado é invalido"),
    SPLIT_AMOUNT_MUST_BE_GREATER_THAN_ZERO(193, "Valor para realização do SPLIT deve ser superior a 0"),
    SPLIT_ESTABLISHMENT_IS_REQUIRED(194, "SPLIT não habilitado para o cadastro da loja"),
    THE_PLATAFORMID_IS_REQUIRED(195, "Validados de plataformas não enviado"),
    DELIVERYADDRESS_IS_REQUIRED(196, "Campo obrigatório não enviado"),
    STREET_IS_REQUIRED(197, "Campo obrigatório não enviado"),
    NUMBER_IS_REQUIRED(198, "Campo obrigatório não enviado"),
    ZIPCODE_IS_REQUIRED(199, "Campo obrigatório não enviado"),
    CITY_IS_REQUIRED(200, "Campo obrigatório não enviado"),
    STATE_IS_REQUIRED(201, "Campo obrigatório não enviado"),
    DISTRICT_IS_REQUIRED(202, "Campo obrigatório não enviado"),
    CART_ITEM_NAME_IS_REQUIRED(203, "Campo obrigatório não enviado"),
    CART_ITEM_QUANTITY_IS_REQUIRED(204, "Campo obrigatório não enviado"),
    CART_ITEM_TYPE_IS_REQUIRED(205, "Campo obrigatório não enviado"),
    CART_ITEM_NAME_LENGTH_EXCEEDED(206, "Dado enviado excede o tamanho do campo"),
    CART_ITEM_DESCRIPTION_LENGTH_EXCEEDED(207, "Dado enviado excede o tamanho do campo"),
    CART_ITEM_SKU_LENGTH_EXCEEDED(208, "Dado enviado excede o tamanho do campo"),
    SHIPPING_ADDRESSEE_SKU_LENGTH_EXCEEDED(209, "Dado enviado excede o tamanho do campo"),
    SHIPPING_DATA_CANNOT_BE_NULL(210, "Campo obrigatório não enviado"),
    WALLETKEY_IS_INVALID(211, "Dados da Visa Checkout invalidos"),
    MERCHANT_WALLET_CONFIGURATION_NOT_FOUND_212(212, "Dado de Wallet enviado não é valido"),
    MERCHANT_WALLET_CONFIGURATION_NOT_FOUND_213(213, "Cartão de crédito enviado é invalido"),
    CREDIT_CARD_HOLDER_MUST_HAVE_ONLY_LETTERS(214, "Portador do cartão não deve conter caracteres especiais"),
    AGENCY_IS_REQUIRED_IN_BOLETO_CREDENTIAL(215, "Campo obrigatório não enviado"),
    CUSTOMER_IP_ADDRESS_IS_INVALID(216, "IP bloqueado por questões de segurança"),
    MERCHANTID_WAS_NOT_FOUND(300, "—"),
    REQUEST_IP_IS_NOT_ALLOWED(301, "—"),
    SENT_MERCHANTORDERID_IS_DUPLICATED(302, "—"),
    SENT_ORDERID_DOES_NOT_EXIST(303, "—"),
    CUSTOMER_IDENTITY_IS_REQUIRED(304, "—"),
    MERCHANT_IS_BLOCKED(306, "—"),
    TRANSACTION_NOT_FOUND(307, "Transação não encontrada ou não existente no ambiente."),
    TRANSACTION_NOT_AVAILABLE_TO_CAPTURE(308, "Transação não pode ser capturada - Entre em contato com o suporte Cielo"),
    TRANSACTION_NOT_AVAILABLE_TO_VOID(309, "Transação não pode ser Cancelada - Entre em contato com o suporte Cielo"),
    PAYMENT_METHOD_DOEST_NOT_SUPPORT_THIS_OPERATION(310, "Comando enviado não suportado pelo meio de pagamento"),
    REFUND_IS_NOT_ENABLED_FOR_THIS_MERCHANT(311, "Cancelamento após 24 horas não liberado para o lojista"),
    TRANSACTION_NOT_AVAILABLE_TO_REFUND(312, "Transação não permite cancelamento após 24 horas"),
    RECURRENT_PAYMENT_NOT_FOUND_313(313, "Transação recorrente não encontrada ou não disponivel no ambiente"),
    INVALID_INTEGRATION(314, "—"),
    CANNOT_CHANGE_NEXTRECURRENCY_WITH_PENDING_PAYMENT(315, "—"),
    CANNOT_SET_NEXTRECURRENCY_TO_PAST_DATE(316, "Não é permitido alterada dada da recorrencia para uma data passada"),
    INVALID_RECURRENCY_DAY(317, "—"),
    NO_TRANSACTION_FOUND(318, "—"),
    SMART_RECURRENCY_IS_NOT_ENABLED(319, "Recorrencia não vinculada ao cadastro do lojista"),
    CAN_NOT_UPDATE_AFFILIATION_BECAUSE_THIS_RECURRENCY_NOT_AFFILIATION_SAVED(320, "—"),
    CAN_NOT_SET_ENDDATE_TO_BEFORE_NEXT_RECURRENCY(321, "—"),
    ZERO_DOLLAR_AUTH_IS_NOT_ENABLED(322, "Zero Dollar não vinculado ao cadastro do lojista"),
    BIN_QUERY_IS_NOT_ENABLED(323, "Consulta de Bins não vinculada ao cadastro do lojista");

    @Getter
    private int code;

    @Getter
    private String description;

    private CieloApiErrorCode(int code, String description)
    {
        this.code = code;
        this.description = description;
    }

    public static CieloApiErrorCode from(Integer status) {
        Optional<CieloApiErrorCode> paymentStatus = Arrays.asList(values()).stream()
                .filter( s -> s.code == status)
                .findFirst();

        if(paymentStatus.isPresent())
        {
            return paymentStatus.get();
        }
        else
        {
            throw new IllegalArgumentException("OrderStatus da Cielo nao mapeado: " + status);
        }
    }
}
