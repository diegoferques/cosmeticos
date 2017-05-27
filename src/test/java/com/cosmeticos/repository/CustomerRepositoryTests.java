package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

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
        c1.setBirthDate(LocalDate.of(1980, 01, 20).toString());
        c1.setCellPhone("(21) 98877-6655");
        c1.setCpf("098.765.432-10");
        c1.setDateRegister(LocalDate.now().toString());
        c1.setGenre('M');
        c1.setIdAddress(null);
        //c1.setIdCustomer(Long.valueOf(1));
        c1.setIdLogin(null);
        c1.setNameCustomer("João da Silva");
        c1.setServiceRequestCollection(null);
        c1.setStatus((short) 1);

        Customer c2 = new Customer();
        c2.setBirthDate(LocalDate.of(1981, 10, 21).toString());
        c2.setCellPhone("(21) 98807-2756");
        c2.setCpf("098.330.987-62");
        c2.setDateRegister(LocalDate.now().toString());
        c2.setGenre('M');
        c2.setIdAddress(null);
        //c2.setIdCustomer(Long.valueOf(2));
        c2.setIdLogin(null);
        c2.setNameCustomer("Diego Fernandes");
        c2.setServiceRequestCollection(null);
        c2.setStatus((short) 1);

        Customer c3 = new Customer();
        c3.setBirthDate(LocalDate.of(1975, 07, 13).toString());
        c3.setCellPhone("(21) 99988-7766");
        c3.setCpf("831.846.135-15");
        c3.setDateRegister(LocalDate.now().toString());
        c3.setGenre('F');
        c3.setIdAddress(null);
        //c3.setIdCustomer(Long.valueOf(3));
        c3.setIdLogin(null);
        c3.setNameCustomer("Maria das Dores");
        c3.setServiceRequestCollection(null);
        c3.setStatus((short) 1);

        Customer c4 = new Customer();
        c4.setBirthDate(LocalDate.of(1991, 12, 03).toString());
        c4.setCellPhone("(21) 99887-7665");
        c4.setCpf("816.810.695-68");
        c4.setDateRegister(LocalDate.now().toString());
        c4.setGenre('F');
        c4.setIdAddress(null);
        //c4.setIdCustomer(Long.valueOf(4));
        c4.setIdLogin(null);
        c4.setNameCustomer("Fernanda Cavalcante");
        c4.setServiceRequestCollection(null);
        c4.setStatus((short) 0);

        Customer c5 = new Customer();
        c5.setBirthDate(LocalDate.of(2001, 07, 28).toString());
        c5.setCellPhone("(21) 97766-5544");
        c5.setCpf("541.913.254-81");
        c5.setDateRegister(LocalDate.now().toString());
        c5.setGenre('M');
        c5.setIdAddress(null);
        //c5.setIdCustomer(Long.valueOf(5));
        c5.setIdLogin(null);
        c5.setNameCustomer("José das Couves");
        c5.setServiceRequestCollection(null);
        c5.setStatus((short) 1);
    }

    @Test
    public void testCustomer1() {
        Customer customer = repository.findByIdCustomer(Long.valueOf(1));
        Assert.assertNotNull(customer);

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1980-01-20", customer.getBirthDate());
        Assert.assertEquals("(21) 98877-6655", customer.getCellPhone());
        Assert.assertEquals("098.765.432-10", customer.getCpf());
        Assert.assertEquals('M', customer.getGenre());
        //Assert.assertEquals(1, customer.getStatus());
    }
}
