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

    private Customer c1, c2, c3,  c4, c5;

    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() throws ParseException {

    }

    @Test
    public void testCustomer1() {

        User u1 = new  User("uu1", "u1pass", "uu1@gmail.com");
        Address address = new Address();

        //TODO: pesquisar como gravar apenas dia mes e ano.
        //c1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        //Date birthDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("1980-01-20");

        c1 = new Customer();
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCpf("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameCustomer("Test João da Silva");
        //c1.setOrderCollection(null);
        c1.setStatus(Customer.Status.ACTIVE.ordinal());
        c1.setUser(u1);
        c1.setAddress(address);

        u1.setCustomer(c1);
        u1.setPersonType(User.PersonType.FISICA);
        address.setCustomer(c1);

        repository.save(c1);

        Customer customer = repository.findById(c1.getIdCustomer()).get();
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getAddress());
        Assert.assertNotNull(customer.getUser());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1980-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 98877-6655", customer.getCellPhone());
        Assert.assertEquals("098.765.432-10", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals(Character.valueOf('M'), customer.getGenre());
        Assert.assertEquals("Test João da Silva", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
        Assert.assertEquals(User.PersonType.FISICA, u1.getPersonType());

    }

    @Test
    public void testCustomer2() throws ParseException {

        User u2 = new  User("uu2", "u2pass", "uu2@gmail.com");
        Address address2 = new Address();

        Date birthDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("1981-01-20");
        c2 = new Customer();
        c2.setBirthDate(birthDate2);
        c2.setCellPhone("(21) 98807-2756");
        c2.setCpf("098.330.987-62");
        c2.setDateRegister(Calendar.getInstance().getTime());
        c2.setGenre('M');
        c2.setNameCustomer("Test Diego Fernandes");
        //c2.setOrderCollection(null);
        c2.setStatus(Customer.Status.ACTIVE.ordinal());


        c2.setAddress(address2);
        c2.setUser(u2);

        u2.setCustomer(c2);
        u2.setPersonType(User.PersonType.JURIDICA);
        address2.setCustomer(c2);

        repository.save(c2);

        Customer customer = repository.findById(c2.getIdCustomer()).get();
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getAddress());
        Assert.assertNotNull(customer.getUser());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1981-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 98807-2756", customer.getCellPhone());
        Assert.assertEquals("098.330.987-62", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals(Character.valueOf('M'), customer.getGenre());
        Assert.assertEquals("Test Diego Fernandes", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
        Assert.assertEquals(User.PersonType.JURIDICA, u2.getPersonType());

    }

    @Test
    public void testCustomer3() throws ParseException {

        User u3 = new  User("uu3", "u3pass", "uu3@gmail.com");
        Address address3 = new Address();

        Date birthDate3 = new SimpleDateFormat("yyyy-MM-dd").parse("1982-01-20");
        c3 = new Customer();
        c3.setBirthDate(birthDate3);
        c3.setCellPhone("(21) 99988-7766");
        c3.setCpf("831.846.135-15");
        c3.setDateRegister(Calendar.getInstance().getTime());
        c3.setGenre('F');
        c3.setNameCustomer("Test Maria das Dores");
        //c3.setOrderCollection(null);
        c3.setStatus(Customer.Status.ACTIVE.ordinal());


        c3.setAddress(address3);
        c3.setUser(u3);

        u3.setCustomer(c3);
        u3.setPersonType(User.PersonType.FISICA);
        address3.setCustomer(c3);

        repository.save(c3);


        Customer customer = repository.findById(c3.getIdCustomer()).get();
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getAddress());
        Assert.assertNotNull(customer.getUser());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1982-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 99988-7766", customer.getCellPhone());
        Assert.assertEquals("831.846.135-15", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals(Character.valueOf('F'), customer.getGenre());
        Assert.assertEquals("Test Maria das Dores", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
        Assert.assertEquals(User.PersonType.FISICA, u3.getPersonType());

    }

    @Test
    public void testCustomer4() throws ParseException {

        User u4 = new  User("uu4", "u4pass", "uu4@gmail.com");
        Address address4 = new Address();

        Date birthDate4 = new SimpleDateFormat("yyyy-MM-dd").parse("1983-01-20");
        c4 = new Customer();
        c4.setBirthDate(birthDate4);
        c4.setCellPhone("(21) 99887-7665");
        c4.setCpf("816.810.695-68");
        c4.setDateRegister(Calendar.getInstance().getTime());
        c4.setGenre('F');
        c4.setNameCustomer("Test Fernanda Cavalcante");
        //c4.setOrderCollection(null);
        c4.setStatus(Customer.Status.INACTIVE.ordinal());

        c4.setAddress(address4);
        c4.setUser(u4);

        u4.setCustomer(c4);
        address4.setCustomer(c4);
        u4.setPersonType(User.PersonType.JURIDICA);

        repository.save(c4);



        Customer customer = repository.findById(c4.getIdCustomer()).get();
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getAddress());
        Assert.assertNotNull(customer.getUser());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1983-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 99887-7665", customer.getCellPhone());
        Assert.assertEquals("816.810.695-68", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals(Character.valueOf('F'), customer.getGenre());
        Assert.assertEquals("Test Fernanda Cavalcante", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.INACTIVE.ordinal(), (int)customer.getStatus());
        Assert.assertEquals(User.PersonType.JURIDICA, u4.getPersonType());
    }

    @Test
    public void testCustomer5() throws ParseException {

        User u5 = new  User("uu5", "u5pass", "uu5@gmail.com");
        Address address5 = new Address();
        Date birthDate5 = new SimpleDateFormat("yyyy-MM-dd").parse("1984-01-20");
        c5 = new Customer();
        c5.setBirthDate(birthDate5);
        c5.setCellPhone("(21) 97766-5544");
        c5.setCpf("541.913.254-81");
        c5.setDateRegister(Calendar.getInstance().getTime());
        c5.setGenre('M');
        c5.setNameCustomer("Test José das Couves");
        c5.setStatus(Customer.Status.ACTIVE.ordinal());
        c5.setAddress(address5);
        c5.setUser(u5);

        u5.setCustomer(c5);
        u5.setPersonType(User.PersonType.FISICA);
        address5.setCustomer(c5);

        repository.save(c5);


        Customer customer = repository.findById(c5.getIdCustomer()).get();
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getAddress());
        Assert.assertNotNull(customer.getUser());

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("1984-01-20 00:00:00.0", customer.getBirthDate().toString());
        Assert.assertEquals("(21) 97766-5544", customer.getCellPhone());
        Assert.assertEquals("541.913.254-81", customer.getCpf());
        //Assert.assertEquals("", customer.getDateRegister());
        Assert.assertEquals(Character.valueOf('M'), customer.getGenre());
        Assert.assertEquals("Test José das Couves", customer.getNameCustomer());
        Assert.assertEquals(Customer.Status.ACTIVE.ordinal(), (int)customer.getStatus());
        Assert.assertEquals(User.PersonType.FISICA, u5.getPersonType());

    }

    //TRECHO ABAIXO COMENTADO, POIS ESTAVA DANDO NULLPOINTER. TIVE QUE REESCREVER CÓDIGO
    /*
    private Address createFakeAddress(Customer customer) {
        CustomerService cs = new CustomerService();
        return cs.createFakeAddress(customer);
    }

    private User createFakeUser(Customer customer) {
        CustomerService cs = new CustomerService();
        return cs.createFakeUser(customer);
    }
    */

    public User createFakeLogin(Customer c) {
        User u = new User();
        u.setEmail("diego@bol.com");
        //u.setUser(1234L);
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
        a.setComplement("HOUSE");
        //a.getCustomerCollection().add(customer);
        addressRepository.save(a);
        return a;
    }


}
