package com.cosmeticos.cielo;

import com.cosmeticos.payment.cielo.CieloTransactionClient;
import com.cosmeticos.payment.cielo.model.*;
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
public class CieloIntegrationCaptureTest {

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

    /**
     * Testa captura de uma resertva (autorizacao)
     * @throws Exception
     */
    @Test
    public void captureSucess() throws Exception {

        mockServer.when(HttpRequest.request()
                .withMethod("PUT")
                .withPath("/1/sales/1231321/capture")
                .withHeader("merchantId","1234")
                .withHeader("merchantKey","abcd"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBody("{\n" +
                                "    \"OrderStatus\": 2,\n" +
                                "    \"ReturnCode\": \"6\",\n" +
                                "    \"ReturnMessage\": \"Operation Successful\",\n" +
                                "    \"Links\": [\n" +
                                "        {\n" +
                                "            \"Method\": \"GET\",\n" +
                                "            \"Rel\": \"self\",\n" +
                                "            \"Href\": \"https://apiquerysandbox.cieloecommerce.cielo.com.br/1/sales/{PaymentId}\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "            \"Method\": \"PUT\",\n" +
                                "            \"Rel\": \"void\",\n" +
                                "            \"Href\": \"https://apisandbox.cieloecommerce.cielo.com.br/1/sales/{PaymentId}/void\"\n" +
                                "        }\n" +
                                "    ]\n" +
                                "}"));

        CaptureResponse captureResponse = transactionClient.capture(null, "1231321");

        Assertions.assertThat(captureResponse.getStatus()).isEqualTo(2);
        Assertions.assertThat(captureResponse.getReturnCode()).isEqualTo("6");
    }
}
