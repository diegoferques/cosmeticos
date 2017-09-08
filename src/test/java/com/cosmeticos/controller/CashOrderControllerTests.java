package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalCategoryRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import com.cosmeticos.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Vinicius on 17/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CashOrderControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @MockBean
    private PaymentService paymentService;

    @Test
    public void testReady2ChargeToSemiClosed() throws URISyntaxException, ParseException, JsonProcessingException {

        Optional<RetornoTransacao> optionalFakeRetornoTransacao = this.getOptionalFakeRetornoTransacao(1);

        Mockito.when(
                paymentService.sendRequest(Mockito.any())
        ).thenReturn(optionalFakeRetornoTransacao);

        Mockito.when(
                paymentService.updatePaymentStatus(Mockito.any())
        ).thenReturn(true);



        Customer c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername(System.nanoTime() + "-createOrderOk" + "-cliente");
        c1.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-cliente@bol");
        Professional professional = ProfessionalControllerTests.createFakeProfessional();
        professional.getUser().setUsername(System.nanoTime()+ "-createOrderOk" + "-professional");
        professional.getUser().setEmail(System.nanoTime()+ "-createOrderOk" + "-professional@bol");

        customerRepository.save(c1);
        professionalRepository.save(professional);

        Category category = categoryRepository.findByName("PEDICURE");
        category = categoryRepository.findWithSpecialties(category.getIdCategory());

        PriceRule pr = null;

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, category);
        ps1.addPriceRule(pr = new PriceRule("Preco de teste 50 mil reais", 5000000));

        professionalCategoryRepository.save(ps1);

        /************ FIM DAS PRE_CONDICOES **********************************/

        String json = OrderJsonHelper.buildJsonCreateNonScheduledOrder(
                c1,
                ps1,
                Payment.Type.CC,
                pr
        );

        System.out.println(json);

        RequestEntity<String> entity =  RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        ResponseEntity<OrderResponseBody> exchange = restTemplate
                .exchange(entity, OrderResponseBody.class);

        Order responsedOrder = exchange.getBody().getOrderList().get(0);

        Assert.assertNotNull(exchange);
        Assert.assertEquals(HttpStatus.OK, exchange.getStatusCode());
        Assert.assertEquals(Order.Status.OPEN, responsedOrder.getStatus());
        Assert.assertFalse(responsedOrder.getPaymentCollection().isEmpty());
        Assert.assertEquals(Payment.Type.CC, responsedOrder.getPaymentCollection().stream().findFirst().get().getType());
        Assert.assertNull(responsedOrder.getScheduleId());


        Order orderOpenOk = exchange.getBody().getOrderList().get(0);

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+orderOpenOk.getIdOrder()+",\n" +
                "    \"status\" : "+ Order.Status.ACCEPTED.ordinal() +"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders")) // put pra atualizar o que inserimos la em cima
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        OrderResponseBody responseBodyDoPut = exchangeUpdate.getBody();
        //ERRO AQUI ABAIXO, VERIFICAR
        Order orderAtualizada = responseBodyDoPut.getOrderList().get(0);

        Assert.assertEquals(HttpStatus.OK, exchangeUpdate.getStatusCode());
        Assert.assertEquals(Order.Status.ACCEPTED, orderAtualizada.getStatus());

        Order createdOrder = exchangeUpdate.getBody().getOrderList().get(0);


        ResponseEntity<OrderResponseBody> exchangeUpdateInProgress = this.updateOrderStatus(
                createdOrder.getIdOrder(), Order.Status.INPROGRESS);

        Assert.assertNotNull(exchangeUpdateInProgress);
        Assert.assertNotNull(exchangeUpdateInProgress.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdateInProgress.getStatusCode());
        Assert.assertEquals(Order.Status.INPROGRESS, exchangeUpdateInProgress.getBody().getOrderList().get(0).getStatus());

        Order updatedOrder = exchangeUpdateInProgress.getBody().getOrderList().get(0);


        ResponseEntity<OrderResponseBody> exchangeUpdaterReady2Charge = this.updateOrderStatus(
                updatedOrder.getIdOrder(), Order.Status.READY2CHARGE);

        Assert.assertNotNull(exchangeUpdaterReady2Charge);
        Assert.assertNotNull(exchangeUpdaterReady2Charge.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdaterReady2Charge.getStatusCode());
        Assert.assertEquals(Order.Status.READY2CHARGE, exchangeUpdaterReady2Charge.getBody().getOrderList().get(0).getStatus());

        Order readyOrder = exchangeUpdateInProgress.getBody().getOrderList().get(0);

        ResponseEntity<OrderResponseBody> exchangeUpdaterSemiClosed = this.updateOrderStatus(
                readyOrder.getIdOrder(), Order.Status.SEMI_CLOSED);

        Assert.assertNotNull(exchangeUpdaterSemiClosed);
        Assert.assertNotNull(exchangeUpdaterSemiClosed.getBody().getOrderList());
        Assert.assertEquals(HttpStatus.OK, exchangeUpdaterSemiClosed.getStatusCode());
        Assert.assertEquals(Order.Status.SEMI_CLOSED, exchangeUpdaterSemiClosed.getBody().getOrderList().get(0).getStatus());
    }

    public ResponseEntity<OrderResponseBody> updateOrderStatus(Long orderId, Order.Status status) throws URISyntaxException {

        String jsonUpdate = "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+ orderId +",\n" +
                "    \"status\" : \""+ status +"\"\n" +
                "\n}\n" +
                "}";

        System.out.println(jsonUpdate);

        RequestEntity<String> entityUpdate =  RequestEntity
                .put(new URI("/orders")) // put pra atualizar o que inserimos la em cima
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(jsonUpdate);

        ResponseEntity<OrderResponseBody> exchangeUpdate = restTemplate
                .exchange(entityUpdate, OrderResponseBody.class);

        return exchangeUpdate;
    }


    private Optional<RetornoTransacao> getOptionalFakeRetornoTransacao(int statusTransacao) {
        RetornoTransacao retornoTransacao = new RetornoTransacao();
        retornoTransacao.setNumeroTransacao(3);
        retornoTransacao.setCodigoEstabelecimento("1501698887865");
        retornoTransacao.setCodigoFormaPagamento(170);
        retornoTransacao.setValor(100);
        retornoTransacao.setValorDesconto(0);
        retornoTransacao.setParcelas(1);
        retornoTransacao.setStatusTransacao(statusTransacao);
        retornoTransacao.setAutorizacao("20170808124436912");
        retornoTransacao.setCodigoTransacaoOperadora("0");
        retornoTransacao.setDataAprovacaoOperadora("2017-08-11 04:56:25");
        retornoTransacao.setNumeroComprovanteVenda("0808124434526");
        retornoTransacao.setNsu("4436912");
        retornoTransacao.setUrlPagamento("1502206705884f8a21ff8-db8f-4c7d-a779-8f35f35cfd71");

        List<String> cartaoUtilizado = new ArrayList<>();
        cartaoUtilizado.add("000000******0001");
        retornoTransacao.setCartoesUtilizados(cartaoUtilizado);

        return Optional.of(retornoTransacao);

    }

}
