package com.cosmeticos.service.order;

import com.cosmeticos.model.Payment;
import com.cosmeticos.payment.ChargeRequest;
import com.cosmeticos.payment.ChargeResponse;
import com.cosmeticos.payment.Charger;
import com.cosmeticos.validation.OrderValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
class PaymentCaptureHelper {

    @Autowired
    @Qualifier("charger")
    private Charger paymentService;

    /**
     * Captura um pagamento caso ele ja nao tenha sido capturado anteriormente.
     * //CARD: https://trello.com/c/G1x4Y97r/101-fluxo-de-captura-de-pagamento-no-superpay
     * //BRANCH: RNF101
     * //BRANCH: RNFapp39-templatando-plus-cartao
     */
    Boolean sendPaymentCapture(Payment payment) throws OrderValidationException {

        ChargeResponse<Object> chargeResponse = paymentService.capture(new ChargeRequest<>(payment));

        switch (chargeResponse.getResponseCode()) {
            case SUCCESS:
            case GATEWAY_DUPLICATE_PAYMENT:
                return true;
            default:
                throw new OrderValidationException(chargeResponse.getResponseCode(), "Falha na captura do superpay.");
        }

    }
}
