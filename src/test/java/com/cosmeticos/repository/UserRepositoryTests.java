package com.cosmeticos.repository;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.Set;

/**
 * Created by matto on 26/05/2017.
 */

/**
 * Created by Vinicius on 29/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private CreditCardRepository ccRepository;

    private Long userId;
    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() {

        CreditCard cc = new CreditCard();
        cc.setToken("1234");
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);

        //User
        User u1 = new User();
        u1.setUsername("KillerUserrepositorytest");
        u1.setPassword("109809876");
        u1.setEmail("KillerUserrepositorytest@gmail.com");
        u1.setSourceApp("facebook");
        u1.setPersonType(User.PersonType.FISICA);
        u1.addCreditCard(cc);

        // Aqui o cascade funciona
        userId = userRepository.save(u1).getIdLogin();

        Assert.assertEquals(1, u1.getCreditCardCollection().size());
        Assert.assertNotNull
        (
                u1.getCreditCardCollection()
                .stream()
                .findFirst()
                .get()
                .getIdCreditCard()
        );
    }

    /**
     * Teste motivado pelo problema descrito em
     * https://www.mkyong.com/hibernate/cascade-jpa-hibernate-annotation-common-mistake/
     *
     * A solucao fonecida pelo site tambem nao funciona. Tvemos q apelar pra um creditCardRepository
     */
    @Test
    @Transactional
    public void testInserirCartaoNovoEmUsuarioQueJaPossuiCartao() {


        CreditCard newCC = new CreditCard();
        newCC.setToken("4321");
        newCC.setVendor("Visa");
        newCC.setStatus(CreditCard.Status.ACTIVE);

        /*
         Aqui o cascade nao funiona. Parece que se ja houver item pesistente na lista, o insert
         do transiente nao ocorre. Eh necessario apelar ao creditCardRepository
          */
        User u = userRepository.findOne(userId);
        u.setPersonType(User.PersonType.JURIDICA);
        u.addCreditCard(newCC);

        creditCardRepository.save(newCC);

        userRepository.save(u);

        Assert.assertEquals(2, u.getCreditCardCollection().size());
        u.getCreditCardCollection().forEach(cc -> Assert.assertNotNull(cc.getIdCreditCard()));
    }

    /**
     * Ignorado ate recebermos resposta no post:
     * https://stackoverflow.com/questions/44915428/java-hibernate-spring-data-cascading-childs-not-working-when-parent-is-persis
     *
     * Recebemos resposta do post acima de descobrimos qual foi o problema.
     */
    @Test
    @Transactional
    public void testInserirCartaoNovoEmUsuarioQueNaoPossuiCartao()
    {

        User u2 = new User();
        u2.setUsername("KILLER sem cartao card 22 maluco");
        u2.setPassword("10980999876");
        u2.setEmail("Killercard22222@gmail.com");
        u2.setSourceApp("facebook");
        u2.setPersonType(User.PersonType.FISICA);

        userRepository.save(u2);

        u2 = userRepository.findOne(u2.getIdLogin());

        CreditCard ccNovo = new CreditCard();
        ccNovo.setToken("77777");
        ccNovo.setVendor("Visa");
        ccNovo.setStatus(CreditCard.Status.ACTIVE);

        u2.addCreditCard(ccNovo);

        userRepository.save(u2);

        Assert.assertEquals("Usuario nao possui 1 carotao como deveria.",
                1, u2.getCreditCardCollection().size());

        Set<CreditCard> ccs = u2.getCreditCardCollection();
        for (CreditCard cc : ccs) {
            Assert.assertNotNull("ID do cartao esta nulo", cc.getIdCreditCard());
        }
    }

    @Test
    @Transactional
    public void testAtualizarTokenDeCartaoJaExistente()
    {


        User u2 = userRepository.findOne(this.userId);
        u2.setPersonType(User.PersonType.JURIDICA);
        u2.getCreditCardCollection().forEach(cc -> cc.setToken("ALTEREI"));

        userRepository.save(u2);

        Set<CreditCard> ccs = u2.getCreditCardCollection();
        for (CreditCard cc : ccs) {
            Assert.assertEquals("Token nao foi alterado", "ALTEREI", cc.getToken());
        }
    }

    @Test
    @Transactional
    public void inativarUmDosCartoesDeUsuarioCom2Cartoes() throws URISyntaxException {

        // Configurcao do usuario q vai ter o cartao alterado
        CreditCard cc1 = new CreditCard();
        cc1.setToken("4321");
        cc1.setVendor("MasterCard");
        cc1.setStatus(CreditCard.Status.ACTIVE);

        CreditCard cc2 = new CreditCard();
        cc2.setToken("1234");
        cc2.setVendor("visa");
        cc2.setStatus(CreditCard.Status.ACTIVE);

        User u1 = new User();
        u1.setUsername("KILLER card 22 maluco");
        u1.setPassword("109809876");
        u1.setEmail("Killercard22@gmail.com");
        u1.setSourceApp("facebook");
        u1.addCreditCard(cc1);
        u1.addCreditCard(cc2);
        u1.setPersonType(User.PersonType.FISICA);

        cc1.setUser(u1);
        cc2.setUser(u1);

        userRepository.saveAndFlush(u1);

        CreditCard ccToBeChanged = u1.getCreditCardCollection()
                .stream()
                .filter(cc -> cc.getToken().equals("1234"))
                .findFirst()
                .get();

        ccToBeChanged.setToken("ALTERADOOOOOOOOOOOOO");
        ccToBeChanged.setStatus(CreditCard.Status.INACTIVE);

        userRepository.saveAndFlush(u1);

        // Conferindo se o usuario ainda possui 2 cartoes apos eu mandar atualizar apenas 1.
        User updatedUser = userRepository.findOne(u1.getIdLogin());
        Assert.assertEquals(2, updatedUser.getCreditCardCollection().size());

        // Verficando se o cartao que mandamos o controller atualizar foi realmente atualizado no banco.
        CreditCard updatedCC = ccRepository.findOne(ccToBeChanged.getIdCreditCard());
        Assert.assertNotNull(updatedCC);
        Assert.assertEquals("ALTERADOOOOOOOOOOOOO", updatedCC.getToken());
        Assert.assertEquals(CreditCard.Status.INACTIVE, updatedCC.getStatus());

    }
}
