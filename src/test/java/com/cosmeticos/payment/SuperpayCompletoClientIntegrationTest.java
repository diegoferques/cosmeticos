package com.cosmeticos.payment;

import com.cosmeticos.Application;
import com.cosmeticos.commons.SuperpayFormaPagamento;
import com.cosmeticos.payment.superpay.SuperpayCompletoClient;
import com.cosmeticos.payment.superpay.ws.completo.CapturarTransacaoCompletaResponse;
import com.cosmeticos.payment.superpay.ws.completo.ObjectFactory;
import com.cosmeticos.payment.superpay.ws.completo.ResultadoPagamentoWS;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.bind.JAXBElement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vinicius on 10/09/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SuperpayCompletoClientIntegrationTest {

    @Autowired
    private SuperpayCompletoClient superpayCompletoClient;

    @MockBean(name = "webServiceTemplateCompleto")
    private WebServiceTemplate webServiceTemplate;

    @Before
    public void setup(){


        ObjectFactory factory = new ObjectFactory();

        ResultadoPagamentoWS resultadoPagamentoWS = new ResultadoPagamentoWS();
        resultadoPagamentoWS.setStatusTransacao(1);

        CapturarTransacaoCompletaResponse response = new CapturarTransacaoCompletaResponse();
        response.setReturn(resultadoPagamentoWS);

        JAXBElement<CapturarTransacaoCompletaResponse> jaxbResponse =
                factory.createCapturarTransacaoCompletaResponse(response);

        Mockito.when(
                webServiceTemplate.marshalSendAndReceive(Mockito.any())
        ).thenReturn(jaxbResponse);


    }

    @IfProfileValue(
            name = Application.ACTIVE_PROFILE_KEY,
            values = { Application.PROFILE_TESTING_INTEGRATION_VALUE }
    )
    @Test
    public void testCapturar(){

        Long numeroTransacao = 323L;
        int codigoFormaPagamento = 2;
        String codigoSeguranca = "12345";
        String dataValidadeCartao = "10/19";
        int idioma = 1;
        String ip = "09876";
        String nomeTitularCarttaoCredito = "NOMEDOTITULAR";
        String origemTransacao = "ORIGEM";
        int parcelas = 1;
        String urlCampainha = "url.com";
        String urlRedirecionamentoNaoPago = "urlRedirecionamentoNaoPago.com";
        String urlRedirecionamentoPago = "urlRedirecionamentoPago.com";
        Long valor = 10000L;
        long valorDesconto = 10L;

        ResultadoPagamentoWS result = (superpayCompletoClient.capturePayment(
                dataValidadeCartao,
                ip,
                nomeTitularCarttaoCredito,
                valor,
                urlCampainha,
                numeroTransacao,
                parcelas,
                SuperpayFormaPagamento.MASTERCARD));

        assertThat(result.getStatusTransacao())
                .isIn(1, 2, 5); // 5=Transacao em Andamento. Acho q tbm vale como OK.


    }
}
