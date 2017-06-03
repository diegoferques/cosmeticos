package com.cosmeticos.repository;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by matto on 26/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerRepositoryTests {

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() throws ParseException {

        //TODO: pesquisar como gravar apenas dia mes e ano.
        //c1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        //Date birthDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-20");

        Customer c1 = new Customer();
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCpf("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameCustomer("João da Silva");
        //c1.setServiceRequestCollection(null);
        c1.setStatus(Customer.Status.ACTIVE.ordinal());
        c1.setIdAddress(this.createFakeAddress(c1));
        c1.setIdLogin(this.createFakeLogin(c1));

        Date birthDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("1981-01-20");
        Customer c2 = new Customer();
        c2.setBirthDate(birthDate2);
        c2.setCellPhone("(21) 98807-2756");
        c2.setCpf("098.330.987-62");
        c2.setDateRegister(Calendar.getInstance().getTime());
        c2.setGenre('M');
        c2.setNameCustomer("Diego Fernandes");
        //c2.setServiceRequestCollection(null);
        c2.setStatus(Customer.Status.ACTIVE.ordinal());
        c2.setIdAddress(this.createFakeAddress(c2));
        c2.setIdLogin(this.createFakeLogin(c2));

        Date birthDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("1982-01-20");
        Customer c3 = new Customer();
        c3.setBirthDate(birthDate3);
        c3.setCellPhone("(21) 99988-7766");
        c3.setCpf("831.846.135-15");
        c3.setDateRegister(Calendar.getInstance().getTime());
        c3.setGenre('F');
        c3.setNameCustomer("Maria das Dores");
        //c3.setServiceRequestCollection(null);
        c3.setStatus(Customer.Status.ACTIVE.ordinal());
        c3.setIdAddress(this.createFakeAddress(c3));
        c3.setIdLogin(this.createFakeLogin(c3));

        Date birthDate4 = new SimpleDateFormat("yyyy-MM-dd").parse("1983-01-20");
        Customer c4 = new Customer();
        c4.setBirthDate(birthDate4);
        c4.setCellPhone("(21) 99887-7665");
        c4.setCpf("816.810.695-68");
        c4.setDateRegister(Calendar.getInstance().getTime());
        c4.setGenre('F');
        c4.setNameCustomer("Fernanda Cavalcante");
        //c4.setServiceRequestCollection(null);
        c4.setStatus(Customer.Status.INACTIVE.ordinal());
        c4.setIdAddress(this.createFakeAddress(c4));
        c4.setIdLogin(this.createFakeLogin(c4));

        Date birthDate5 = new SimpleDateFormat("yyyy-MM-dd").parse("1984-01-20");
        Customer c5 = new Customer();
        c5.setBirthDate(birthDate5);
        c5.setCellPhone("(21) 97766-5544");
        c5.setCpf("541.913.254-81");
        c5.setDateRegister(Calendar.getInstance().getTime());
        c5.setGenre('M');
        c5.setNameCustomer("José das Couves");
        //c5.setServiceRequestCollection(null);
        c5.setStatus(Customer.Status.ACTIVE.ordinal());
        c5.setIdAddress(this.createFakeAddress(c5));
        c5.setIdLogin(this.createFakeLogin(c5));

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
        Assert.assertEquals("1980-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 98877-6655", customer.getCellPhone());
        Assert.assertEquals("098.765.432-10", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals('M', customer.getGenre());
        Assert.assertEquals("João da Silva", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
    }

    @Test
    public void testCustomer2() {
        Customer customer = repository.findOne(2L);
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getIdAddress());
        Assert.assertNotNull(customer.getIdLogin());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1981-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 98807-2756", customer.getCellPhone());
        Assert.assertEquals("098.330.987-62", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals('M', customer.getGenre());
        Assert.assertEquals("Diego Fernandes", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
    }

    @Test
    public void testCustomer3() {
        Customer customer = repository.findOne(3L);
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getIdAddress());
        Assert.assertNotNull(customer.getIdLogin());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1982-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 99988-7766", customer.getCellPhone());
        Assert.assertEquals("831.846.135-15", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals('F', customer.getGenre());
        Assert.assertEquals("Maria das Dores", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
    }

    @Test
    public void testCustomer4() {
        Customer customer = repository.findOne(4L);
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getIdAddress());
        Assert.assertNotNull(customer.getIdLogin());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1983-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 99887-7665", customer.getCellPhone());
        Assert.assertEquals("816.810.695-68", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals('F', customer.getGenre());
        Assert.assertEquals("Fernanda Cavalcante", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.INACTIVE.ordinal(), (int)customer.getStatus());
    }

    @Test
    public void testCustomer5() {
        Customer customer = repository.findOne(5L);
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getIdAddress());
        Assert.assertNotNull(customer.getIdLogin());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1984-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 97766-5544", customer.getCellPhone());
        Assert.assertEquals("541.913.254-81", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals('M', customer.getGenre());
        Assert.assertEquals("José das Couves", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
    }

    //TRECHO ABAIXO COMENTADO, POIS ESTAVA DANDO NULLPOINTER. TIVE QUE REESCREVER CÓDIGO
    /*
    private Address createFakeAddress(Customer customer) {
        CustomerService cs = new CustomerService();
        return cs.createFakeAddress(customer);
    }

    private User createFakeLogin(Customer customer) {
        CustomerService cs = new CustomerService();
        return cs.createFakeLogin(customer);
    }
    */

    public User createFakeLogin(Customer c) {
        User u = new User();
        u.setEmail("diego@bol.com");
        //u.setIdLogin(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername("diegoferques");
        //u.getCustomerCollection().add(c);
        userRepository.save(u);
        return u;
    }

    public Address createFakeAddress(Customer customer) {
        Address a = new Address();
        a.setAddress("Rua Perlita");
        a.setCep("0000000");
        a.setCity("RJO");
        a.setCountry("BRA");
        a.setNeighborhood("Austin");
        a.setState("RJ");
        //a.getCustomerCollection().add(customer);
        addressRepository.save(a);
        return a;
    }


}
