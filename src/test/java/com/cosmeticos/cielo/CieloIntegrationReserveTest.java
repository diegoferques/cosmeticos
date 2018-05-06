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
public class CieloIntegrationReserveTest {

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
    public void reserveAndTokenSuccess() throws Exception {

        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/1/sales")
                .withHeader("merchantId","1234")
                .withHeader("merchantKey","abcd"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody("{\n" +
                                "    \"MerchantOrderId\": \"2014111706\",\n" +
                                "    \"Customer\": {\n" +
                                "        \"Name\": \"Comprador crédito simples\"\n" +
                                "    },\n" +
                                "    \"Payment\": {\n" +
                                "        \"ServiceTaxAmount\": 0,\n" +
                                "        \"Installments\": 1,\n" +
                                "        \"Interest\": \"ByMerchant\",\n" +
                                "        \"Capture\": false,\n" +
                                "        \"Authenticate\": false,\n" +
                                "        \"CreditCard\": {\n" +
                                "            \"CardNumber\": \"455187******0183\",\n" +
                                "            \"Holder\": \"Teste Holder\",\n" +
                                "            \"ExpirationDate\": \"12/2030\",\n" +
                                "            \"SaveCard\": false,\n" +
                                "            \"Brand\": \"Visa\"\n" +
                                "        },\n" +
                                "        \"ProofOfSale\": \"674532\",\n" +
                                "        \"Tid\": \"0305023644309\",\n" +
                                "        \"AuthorizationCode\": \"123456\",\n" +
                                "        \"PaymentId\": \"24bc8366-fc31-4d6c-8555-17049a836a07\",\n" +
                                "        \"Type\": \"CreditCard\",\n" +
                                "        \"Amount\": 15700,\n" +
                                "        \"Currency\": \"BRL\",\n" +
                                "        \"Country\": \"BRA\",\n" +
                                "        \"ExtraDataCollection\": [],\n" +
                                "        \"Status\": 1,\n" +
                                "        \"ReturnCode\": \"4\",\n" +
                                "        \"ReturnMessage\": \"Operation Successful\",\n" +
                                "        \"Links\": [\n" +
                                "            {\n" +
                                "                \"Method\": \"GET\",\n" +
                                "                \"Rel\": \"self\",\n" +
                                "                \"Href\": \"https://apiquerysandbox.cieloecommerce.cielo.com.br/1/sales/{PaymentId}\"\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"Method\": \"PUT\",\n" +
                                "                \"Rel\": \"capture\",\n" +
                                "                \"Href\": \"https://apisandbox.cieloecommerce.cielo.com.br/1/sales/{PaymentId}/capture\"\n" +
                                "            },\n" +
                                "            {\n" +
                                "                \"Method\": \"PUT\",\n" +
                                "                \"Rel\": \"void\",\n" +
                                "                \"Href\": \"https://apisandbox.cieloecommerce.cielo.com.br/1/sales/{PaymentId}/void\"\n" +
                                "            }\n" +
                                "        ]\n" +
                                "    }\n" +
                                "}"));


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

        Assertions.assertThat(authorizeResponse.getPayment().getStatus()).isEqualTo(1);
    }

    @Test(expected = OrderException.class)
    public void reserveAndTokenServerFail() throws Exception {

        mockServer.when(HttpRequest.request()
                .withMethod("POST")
                .withPath("/1/sales")
                .withHeader("merchantId","1234")
                .withHeader("merchantKey","abcd"))
                .respond(response()
                        .withStatusCode(500)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody("[\n" +
                                "    {\n" +
                                "        \"Code\": 126,\n" +
                                "        \"Message\": \"Credit Card Expiration Date is invalid\"\n" +
                                "    }\n" +
                                "]"));


        CieloCustomer cieloCustomer = CieloCustomer.builder()
                .build();

        RequestCieloPayment cieloPayment = RequestCieloPayment.builder()
                .build();

        AuthorizeAndTokenRequest authorizeAndTokenRequest = AuthorizeAndTokenRequest.builder()
                .customer(cieloCustomer)
                .merchantOrderId("1231321")
                .payment(cieloPayment)
                .build();

        AuthorizeAndTokenResponse authorizeResponse = null;
        authorizeResponse = transactionClient.reserve(null, authorizeAndTokenRequest);

    }

}
