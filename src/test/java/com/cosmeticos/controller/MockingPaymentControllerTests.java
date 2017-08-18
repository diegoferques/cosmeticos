package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Created by matto on 17/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingPaymentControllerTests {

    @MockBean
    private PaymentController paymentController;

    //TODO - COMO ESSE TESTE CHAMA A URL DA SUPERPAY, DEVEMOS COLOCAR EM UMA NOVA CLASSE DE TESTES MOCKADA
    @Test
    public void testCapturarTransacaoOK() throws URISyntaxException, ParseException, JsonProcessingException {

        Mockito.when(
                paymentController.capturaTransacao(Mockito.any())
        ).thenReturn(true);

        Long numeroTransacao = 3L;

        Boolean capturaTransacao = paymentController.capturaTransacao(numeroTransacao);

        Assert.assertNotNull(capturaTransacao);
        Assert.assertEquals(true, capturaTransacao);

    }
}
