package com.cosmeticos.repository;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
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

    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() {

        Address address = new Address();

        Professional c1 = new Professional();
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCnpj("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameProfessional("João da Silva");
        c1.setStatus(Professional.Status.ACTIVE);

        c1.setIdAddress(address);
        address.setProfessional(c1);

        repository.save(c1);
    }

    @Test
    public void testFindByIdEquals1() {
        Professional customer = repository.findOne(1L);

        Assert.assertNotNull(customer);

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("João da Silva", customer.getNameProfessional());
    }


}
