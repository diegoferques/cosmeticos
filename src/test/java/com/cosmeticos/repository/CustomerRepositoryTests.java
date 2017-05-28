package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by matto on 26/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository repository;

    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() {
        Customer c1 = new Customer();
        //TODO: pesquisar como gravar apenas dia mes e ano.
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCpf("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameCustomer("João da Silva");
        c1.setServiceRequestCollection(null);
        c1.setStatus(Customer.Status.ACTIVE.ordinal());

        Customer c2 = new Customer();
        c2.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1981, 10, 21, 0, 0, 0)));
        c2.setCellPhone("(21) 98807-2756");
        c2.setCpf("098.330.987-62");
        c2.setDateRegister(Calendar.getInstance().getTime());
        c2.setGenre('M');
        c2.setNameCustomer("Diego Fernandes");
        c2.setServiceRequestCollection(null);
        c2.setStatus(Customer.Status.ACTIVE.ordinal());

        Customer c3 = new Customer();
        c3.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1975, 10, 21, 0, 0, 0)));
        c3.setCellPhone("(21) 99988-7766");
        c3.setCpf("831.846.135-15");
        c3.setDateRegister(Calendar.getInstance().getTime());
        c3.setGenre('F');
        c3.setNameCustomer("Maria das Dores");
        c3.setServiceRequestCollection(null);
        c3.setStatus(Customer.Status.ACTIVE.ordinal());

        Customer c4 = new Customer();
        c4.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
        c4.setCellPhone("(21) 99887-7665");
        c4.setCpf("816.810.695-68");
        c4.setDateRegister(Calendar.getInstance().getTime());
        c4.setGenre('F');
        c4.setNameCustomer("Fernanda Cavalcante");
        c4.setServiceRequestCollection(null);
        c4.setStatus(Customer.Status.INACTIVE.ordinal());

        Customer c5 = new Customer();
        c5.setBirthDate(Timestamp.valueOf(LocalDateTime.of(2001, 10, 21, 0, 0, 0)));
        c5.setCellPhone("(21) 97766-5544");
        c5.setCpf("541.913.254-81");
        c5.setDateRegister(Calendar.getInstance().getTime());
        c5.setGenre('M');
        c5.setNameCustomer("José das Couves");
        c5.setServiceRequestCollection(null);
        c5.setStatus(Customer.Status.ACTIVE.ordinal());

        repository.save(c1);
        repository.save(c2);
        repository.save(c3);
        repository.save(c4);
        repository.save(c5);
    }

    @Test
    public void testCustomer1() {
        Customer customer = repository.findOne(1L);
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getIdAddress());
        Assert.assertNotNull(customer.getIdLogin());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1980-01-20", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 98877-6655", customer.getCellPhone());
        Assert.assertEquals("098.765.432-10", customer.getCpf());
        Assert.assertEquals('M', customer.getGenre());
        //Assert.assertEquals(1, customer.getStatus());
    }


}
