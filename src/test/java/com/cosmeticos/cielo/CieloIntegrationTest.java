package com.cosmeticos.cielo;

import com.cosmeticos.cielo.model.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CieloIntegrationTest {

    @Autowired
    private CieloTransactionClient transactionClient;

    @Test
    public void reserveAndToken() throws Exception {
        CieloCustomer cieloCustomer = CieloCustomer.builder()
                .build();

        RequestCieloPayment cieloPayment = RequestCieloPayment.builder()
                .build();

        AuthorizeAndTokenRequest authorizeAndTokenRequest = AuthorizeAndTokenRequest.builder()
                .customer(cieloCustomer)
                .merchantOrderId("1231321")
                .payment(cieloPayment)
                .build();

        AuthorizeAndTokenResponse authorizeResponse = transactionClient.reserve(null, authorizeAndTokenRequest);

        Assertions.assertThat(authorizeResponse.getPayment().getStatus()).isEqualTo(2);
    }

    /**
     * Testa captura de uma resertva (autorizacao)
     * @throws Exception
     */
    @Test
    public void capture() throws Exception {
        CieloCustomer cieloCustomer = CieloCustomer.builder()
                .build();

        RequestCieloPayment cieloPayment = RequestCieloPayment.builder()
                .build();

        AuthorizeAndTokenRequest authorizeAndTokenRequest = AuthorizeAndTokenRequest.builder()
                .customer(cieloCustomer)
                .merchantOrderId("1231321")
                .payment(cieloPayment)
                .build();

        AuthorizeAndTokenResponse authorizeResponse = transactionClient.reserve(null, authorizeAndTokenRequest);

        CaptureResponse captureResponse = transactionClient.capture(null, authorizeResponse.getPayment().getPaymentId());

        Assertions.assertThat(captureResponse.getStatus()).isEqualTo(2);
        Assertions.assertThat(captureResponse.getReturnCode()).isEqualTo("6");
    }
}
