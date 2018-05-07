package com.cosmeticos.cielo;

import com.cosmeticos.payment.cielo.CieloTransactionClient;
import com.cosmeticos.payment.cielo.model.*;
import com.cosmeticos.validation.OrderException;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CieloIntegrationAddCardTest {

    private ClientAndServer mockServer;

    @Autowired
    private CieloTransactionClient transactionClient;

    @Before
    public void setUp() throws Exception {

        mockServer = startClientAndServer(9000);
    }

    @After
    public void after() {
        mockServer.stop();
    }

    @Test
    public void addCardSuccess() throws Exception {

        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/1/card")
                .withHeader("merchantId","1234")
                .withHeader("merchantKey","abcd"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody("{\n" +
                                "  \"CardToken\": \"db62dc71-d07b-4745-9969-42697b988ccb\",\n" +
                                "  \"Links\": {\n" +
                                "    \"Method\": \"GET\",\n" +
                                "    \"Rel\": \"self\",\n" +
                                "    \"Href\": \"https://apiquerydev.cieloecommerce.cielo.com.br/1/card/db62dc71-d07b-4745-9969-42697b988ccb\"}\n" +
                                "}"));


        String nomeTitularCartaoCredito = "Fulado da Silva";
        String numeroCartaoCredito = "0000000000000000";
        String dataValidadeCartao = "09/22";
        String brand = "VISA";

        CieloAddCardRequestBody addCardRequestBody = CieloAddCardRequestBody.builder()
                .cardNumber(numeroCartaoCredito)
                .brand(brand)
                .customerName(nomeTitularCartaoCredito)
                .expirationDate(dataValidadeCartao)
                .build();

        CieloAddCardResponseBody cieloAddCardResponseBody = transactionClient.addCard(addCardRequestBody);

        Assertions.assertThat(cieloAddCardResponseBody.getToken()).isEqualTo("db62dc71-d07b-4745-9969-42697b988ccb");
    }


}
