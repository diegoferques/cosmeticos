package com.cosmeticos.repository;

import com.cosmeticos.model.*;
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
import java.util.List;

/**
 * Created by matto on 26/05/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfessionalRepositoryTests {

    @Autowired
    private ProfessionalRepository repository;
 private  long id;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WalletRepository walletRepository;

    /**
     * Inicializa o H2 com dados iniciais.
     */
    @Before
    public void setupTests() {

    }

    @Test
    public void testFindByNameEqualJoaoDaSilva123() {

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

        Professional customer = repository.findOne(id);

        Assert.assertNotNull(customer);

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("João da Silva 123", customer.getNameProfessional());

    }

    @Test
    public void testWalletIncresing()
    {
        Customer c1 = customerRepository.findOne(1L);
        Customer c2 = customerRepository.findOne(2L);

        Wallet cw1 = new Wallet();
        cw1.getCustomers().add(c1);
        cw1.getCustomers().add(c2);

        Professional p1 = new Professional();
        p1.setNameProfessional("Garry");
        p1.setAddress(new Address());
        p1.setUser(new User("garrydias", "123qwe", "garrydias@bol"));
        p1.setWallet(cw1);
        cw1.setProfessional(p1);

        repository.save(p1);

        // Se o idCustomerWallet nao for nulo, significa que o hibernate inseriu em cascata.
        Assert.assertNotNull(p1.getWallet().getIdWallet());
    }

    //LOCALIZANDO OS DOIS PROFESSIONAIS INSERIDOS NO ITEM 1 DO CARD 39
    @Test
    public void testFindByNameEqualFubanga() {

        User user6 = new User("fubangamaloca", "123abc", "joana6@bol");
        Address address6 = new Address();
        address6.setAddress("Travessa Tuviassuiara, 32");
        address6.setNeighborhood("Rodilândia");
        address6.setCity("Nova Iguaçu");
        address6.setState("Rio de Janeiro");
        address6.setCountry("Brazil");
        address6.setCep("26083-285");
        address6.setComplement("HOUSE");

        Professional s6 = new Professional();
        s6.setNameProfessional("fubangamaloca");
        s6.setAddress(address6);
        s6.setUser(user6);

        address6.setProfessional(s6);
        user6.setProfessional(s6);

        repository.save(s6);

        User user7 = new User("bocada", "123abc", "joao7@bol");
        Address address7 = new Address();
        address7.setAddress("Avenida Marechal Floriano, 46");
        address7.setNeighborhood("Centro ");
        address7.setCity("Rio de Janeiro/");
        address7.setState("Rio de Janeiro");
        address7.setCountry("Brazil");
        address7.setCep("20080-001");
        address7.setComplement("HOUSE");

        Professional s7 = new Professional();
        s7.setNameProfessional("bocada");
        s7.setAddress(address7);
        s7.setUser(user7);

        address7.setProfessional(s7);
        user7.setProfessional(s7);

        repository.save(s7);

        List<Professional> p1 = repository.findByNameProfessional("fubangamaloca");
        List<Professional> p2 = repository.findByNameProfessional("bocada");

        Assert.assertEquals(
                "IDs de endereco divergentes",
                address6.getIdAddress(),
                p1.get(0).getAddress().getIdAddress()
        );

        Assert.assertEquals(
                "IDs de endereco divergentes",
                address7.getIdAddress(),
                p2.get(0).getAddress().getIdAddress()
        );

        Assert.assertNotNull(p1.get(0));
        Assert.assertNotNull(p2.get(0));

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("fubangamaloca", p1.get(0).getNameProfessional());
        Assert.assertEquals("bocada", p2.get(0).getNameProfessional());

        Assert.assertEquals("26083-285", p1.get(0).getAddress().getCep());
        Assert.assertEquals("20080-001", p2.get(0).getAddress().getCep());

    }

    /*
    @Test
    public void testFindByNameEqualKdoba() {

        List<Professional> p1 = repository.findByNameProfessional("Kelly");
        List<Professional> p2 = repository.findByNameProfessional("Kdoba");

        Assert.assertNotNull(p1.get(0));
        Assert.assertNotNull(p2.get(0));

        // Confere se o Customer que retornou foi o mesmo que foi inserido com id 1.
        Assert.assertEquals("Kelly", p1.get(0).getNameProfessional());
        Assert.assertEquals("Kdoba", p2.get(0).getNameProfessional());

        Assert.assertEquals("26083-285", p1.get(0).getAddress().getCep());
        Assert.assertEquals("20080-001", p2.get(0).getAddress().getCep());
    }
    */
    @Test
    public void testEmployees(){

        User userBoss = new User();

        User userEmployees1 = new User();

        User userEmployees2 = new User();

        Professional boss = new Professional();
        boss.setNameProfessional("boss");
        userBoss.setProfessional(boss);
        boss.setUser(userBoss);

        Professional employee1 = new Professional();
        employee1.setNameProfessional("employee1");
        userEmployees1.setProfessional(employee1);
        employee1.setUser(userEmployees1);


        Professional employee2 = new Professional();
        employee2.setNameProfessional("employee2");
        userEmployees2.setProfessional(employee2);
        employee2.setUser(userEmployees2);

        boss.getEmployeesCollection().add(employee1);
        boss.getEmployeesCollection().add(employee2);

        repository.save(boss);

        Assert.assertNotNull(boss.getEmployeesCollection()
                .stream()
                .findFirst()
                .get()
                .getIdProfessional());

        System.out.println(boss);
        System.out.println(employee1);
        System.out.println(employee2);


    }
}
