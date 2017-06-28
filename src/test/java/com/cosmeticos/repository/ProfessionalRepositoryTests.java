package com.cosmeticos.repository;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by matto on 26/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfessionalRepositoryTests {

    @Autowired
    private ProfessionalRepository repository;
 private  long id;
    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() {

        Address address = new Address();
        User u1 = new User("username123","654321","username123@gmail" );

        Professional p1 = new Professional();
        p1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        p1.setCellPhone("(21) 98877-6655");
        p1.setCnpj("098.765.432-10");
        p1.setDateRegister(Calendar.getInstance().getTime());
        p1.setGenre('M');
        p1.setNameProfessional("João da Silva 123");
        p1.setStatus(Professional.Status.ACTIVE);

        p1.setAddress(address);
        p1.setUser(u1);

        u1.setProfessional(p1);
        address.setProfessional(p1);

        repository.save(p1);

        id = p1.getIdProfessional();
    }

    @Test
    public void testFindByNameEqualJoaoDaSilva123() {
        Professional customer = repository.findOne(id);

        Assert.assertNotNull(customer);

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("João da Silva 123", customer.getNameProfessional());

    }


}
