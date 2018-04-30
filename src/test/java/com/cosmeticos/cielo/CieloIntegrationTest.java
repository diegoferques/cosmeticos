package com.cosmeticos.cielo;

import com.cosmeticos.cielo.model.AuthorizeAndTokenRequest;
import com.cosmeticos.cielo.model.AuthorizeAndTokenResponse;
import com.cosmeticos.cielo.model.CieloCustomer;
import com.cosmeticos.cielo.model.RequestCieloPayment;
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
    public void authorizeAndToken() throws Exception {
        CieloCustomer cieloCustomer = CieloCustomer.builder()
                .build();

        RequestCieloPayment cieloPayment = RequestCieloPayment.builder()
                .build();

        AuthorizeAndTokenRequest authorizeAndTokenRequest = AuthorizeAndTokenRequest.builder()
                .customer(cieloCustomer)
                .merchantOrderId("1231321")
                .payment(cieloPayment)
                .build();

        AuthorizeAndTokenResponse authorizeResponse = transactionClient.authorize(null, authorizeAndTokenRequest);

        Assertions.assertThat(authorizeResponse.getPayment().getStatus()).isEqualTo(2);
    }
}
