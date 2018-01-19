package com.cosmeticos.controller;

import com.cosmeticos.Application;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.repository.*;
import com.cosmeticos.service.OrderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.Exception;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;

import static java.time.LocalDateTime.now;

/**
 * Card que originou esta classe https://trello.com/c/v676aMHw
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockingOrderControllerWithWalletConstraintFailBugTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProfessionalCategoryRepository professionalCategoryRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private CategoryRepository serviceRepository;

    @Autowired
    private WalletRepository walletRepository;

    private Customer c1;

    private Professional professionalA;
    private Professional professionalB;
    private ProfessionalCategory professionalCategoryA, professionalCategoryB;
    private PriceRule priceRuleA, priceRuleB;

    @Before
    public void setUp() throws Exception {

        c1 = CustomerControllerTests.createFakeCustomer();
        c1.getUser().setUsername("testaddwallet-cliente");
        c1.getUser().setEmail("testaddwallet-cliente@bol");
        customerRepository.save(c1);

        professionalA = ProfessionalControllerTests.createFakeProfessional();
        professionalA.getUser().setUsername("testaddwallet-professionalA");
        professionalA.getUser().setEmail("testaddwallet-professionalA@bol");
        professionalRepository.save(professionalA);

        professionalB = ProfessionalControllerTests.createFakeProfessional();
        professionalB.getUser().setUsername("testaddwallet-professionalB");
        professionalB.getUser().setEmail("testaddwallet-professionalB@bol");
        professionalRepository.save(professionalB);

        Category service = serviceRepository.findByName("PEDICURE");
        service = serviceRepository.findWithSpecialties(service.getIdCategory());

        priceRuleA = new PriceRule();
        priceRuleA.setName("RULE");
        priceRuleA.setPrice(7600L);

        priceRuleB = new PriceRule();
        priceRuleB.setName("RULEB");
        priceRuleB.setPrice(8600L);

        professionalCategoryA = new ProfessionalCategory(professionalA, service);
        professionalCategoryA.addPriceRule(priceRuleA);

        professionalCategoryB = new ProfessionalCategory(professionalB, service);
        professionalCategoryB.addPriceRule(priceRuleB);

        // Atualizando associando o Profeissional ao Servico
        professionalCategoryRepository.save(professionalCategoryA);
        professionalCategoryRepository.save(professionalCategoryB);
    }

    @Test public void testaddwallet() throws URISyntaxException {

        // Carteira vazia
        Assert.assertTrue(professionalA.getWallet() == null || professionalA.getWallet().getCustomers().isEmpty());

        String json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryA, priceRuleA, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        // Antes do 1o request a carteira tem que estar vazia.
        // Apos o 2o request a carteira ainda tem q estar vazia.
        Assert.assertTrue(professionalA.getWallet() == null || professionalA.getWallet().getCustomers().isEmpty());

        // Identico ao criado acima...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryA, priceRuleA, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        Wallet wallet = walletRepository.findByProfessional_idProfessional(professionalA.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());


        // Checando se na terceira Order o cliente nao eh inserido duplicado na wallet...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryA, priceRuleA, Payment.Type.CC,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        wallet = walletRepository.findByProfessional_idProfessional(professionalA.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());

        // Checando se na terceira Order o cliente nao eh inserido duplicado na wallet...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryA, priceRuleA, Payment.Type.CC,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        wallet = walletRepository.findByProfessional_idProfessional(professionalA.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());



        /////////////////////////////////////////////////////////////////////////
        ///// Realizando compras com um segundo profissional           //////////
        /////////////////////////////////////////////////////////////////////////

        // Carteira vazia
        Assert.assertTrue(professionalB.getWallet() == null || professionalB.getWallet().getCustomers().isEmpty());

        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryB, priceRuleB, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        // Antes do 1o request a carteira tem que estar vazia.
        // Apos o 2o request a carteira ainda tem q estar vazia.
        Assert.assertTrue(professionalB.getWallet() == null || professionalB.getWallet().getCustomers().isEmpty());

        // Identico ao criado acima...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryB, priceRuleB, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        wallet = walletRepository.findByProfessional_idProfessional(professionalB.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());


        // Checando se na 3a order o cliente nao eh inserido duplicado na wallet...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryB, priceRuleB, Payment.Type.CC,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        wallet = walletRepository.findByProfessional_idProfessional(professionalB.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());
        // Checando se na 4a order o cliente nao eh inserido duplicado na wallet...
        json =
                OrderJsonHelper.buildJsonCreateScheduledOrder(c1, professionalCategoryB, priceRuleB, Payment.Type.CASH,
                        Timestamp.valueOf(now().plusHours(5)).getTime()
                );

        doPostOrder(json);

        wallet = walletRepository.findByProfessional_idProfessional(professionalB.getIdProfessional());//

        Assert.assertTrue(wallet != null && !wallet.getCustomers().isEmpty());
        Assert.assertEquals(1, wallet.getCustomers().size());
    }

    void doPostOrder(String json) throws URISyntaxException {
        RequestEntity<String> entity = RequestEntity
                .post(new URI("/orders"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(json);

        testRestTemplate.exchange(entity, OrderResponseBody.class);
    }

}
