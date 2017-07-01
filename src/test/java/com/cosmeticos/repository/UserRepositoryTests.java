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

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vinicius on 29/06/2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTests {


    @Autowired
    private UserRepository userRepository;
    private Long userId;

    @Before
    public void setup()
    {
        CreditCard cc = new CreditCard();
        cc.setToken("4321");
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);

        User u1 = new User();
        u1.setUsername("KILLER card 22 maluco");
        u1.setPassword("109809876");
        u1.setEmail("Killercard22@gmail.com");
        u1.setSourceApp("facebook");
        u1.getCreditCardCollection().add(cc);
        cc.setUser(u1);

        userId = userRepository.save(u1).getIdLogin();
    }

    @Test
    public void testInserirCartaoNovoEmUsuarioQueJaPossuiCartao()
    {

        User u = userRepository.findOne(userId);

        CreditCard ccNovo = new CreditCard();
        ccNovo.setToken("77777");
        ccNovo.setVendor("Visa");
        ccNovo.setStatus(CreditCard.Status.ACTIVE);

        u.addCreditCard(ccNovo);

        userRepository.save(u);

        Assert.assertEquals("Usuario nao possui 2 carotes como deveria.",
                2, u.getCreditCardCollection().size());

        Set<CreditCard> ccs = u.getCreditCardCollection();
        for (CreditCard cc : ccs) {
            Assert.assertNotNull("ID do cartao esta nulo", cc.getIdCreditCard());
        }
    }

    @Test
    public void testInserirCartaoNovoEmUsuarioQueNaoPossuiCartao()
    {

        User u2 = new User();
        u2.setUsername("KILLER sem cartao card 22 maluco");
        u2.setPassword("10980999876");
        u2.setEmail("Killercard22222@gmail.com");
        u2.setSourceApp("facebook");

         userRepository.save(u2);

        u2 = userRepository.findOne(u2.getIdLogin());

        CreditCard ccNovo = new CreditCard();
        ccNovo.setToken("77777");
        ccNovo.setVendor("Visa");
        ccNovo.setStatus(CreditCard.Status.ACTIVE);

        u2.addCreditCard(ccNovo);

        userRepository.save(u2);

        Assert.assertEquals("Usuario nao possui 2 carotes como deveria.",
                1, u2.getCreditCardCollection().size());

        Set<CreditCard> ccs = u2.getCreditCardCollection();
        for (CreditCard cc : ccs) {
            Assert.assertNotNull("ID do cartao esta nulo", cc.getIdCreditCard());
        }
    }

    @Test
    public void testAtualizarTokenDeCartaoJaExistente()
    {


        User u2 = userRepository.findOne(this.userId);
        u2.getCreditCardCollection().forEach(cc -> cc.setToken("ALTEREI"));

        userRepository.save(u2);

        Set<CreditCard> ccs = u2.getCreditCardCollection();
        for (CreditCard cc : ccs) {
            Assert.assertEquals("Token nao foi alterado", "ALTEREI", cc.getToken());
        }
    }
}
