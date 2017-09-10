package com.cosmeticos.payment;

import com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vinicius on 10/09/2017.
 */
//@ActiveProfiles("integrationTest")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SuperpayCompletoClientIntegrationTest {

    @Autowired
    private SuperpayCompletoClient superpayCompletoClient;

    @Test
    public void testCapturar(){

        String codigoEstabelecimento = "65656565";
        Long numeroTransacao = 123L;
        int operacao = 1;

        ResultadoPagamentoWS result = (superpayCompletoClient.capturePayment(numeroTransacao, operacao));

        assertThat(result.getStatusTransacao())
                .isIn(1, 2, 5); // 5=Transacao em Andamento. Acho q tbm vale como OK.


    }
}
