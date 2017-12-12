package com.cosmeticos.config;

import com.cosmeticos.model.*;
import com.cosmeticos.model.Professional.Type;
import com.cosmeticos.repository.CategoryRepository;
import com.cosmeticos.repository.CustomerRepository;
import com.cosmeticos.repository.ProfessionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

import static java.time.LocalDateTime.now;

/**
 * Classe que so vai executar em dev, pois o profile de producao sera PRODUCTION.
 * Created by Lulu on 26/05/2017.
 */
@Configuration
@Profile("default")
public class ProfessionalPreLoadConfiguration {

    @Autowired
    private ProfessionalRepository repository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @PostConstruct
    public void insertInitialH2Data()
    {
        Customer c1 = customerRepository.findOne(1L);
        Customer c2 = customerRepository.findOne(2L);

        Wallet cw1 = new Wallet();
        cw1.getCustomers().add(c1);
        cw1.getCustomers().add(c2);

        User user1 = new User("garry", "123qwe", "garry@bol");
        user1.addVote(new Vote(3));
        user1.addVote(new Vote(5));
        user1.addVote(new Vote(2));
        user1.addVote(new Vote(2));
        user1.addVote(new Vote(2));
        user1.addVote(new Vote(2));

        Address address1 = new Address();
        address1.setLatitude("-22.7245761");
        address1.setLongitude("-43.51020159999999");


        Professional p1 = new Professional();
        p1.setNameProfessional("Garry");
        p1.setAttendance(Type.HOME_CARE);
        p1.setAddress(address1);
        p1.setDateRegister(Timestamp.valueOf(now()));
        p1.setStatus(Professional.Status.ACTIVE);
        address1.setProfessional(p1);


        // bidirecional reference
        p1.setUser(user1);
        user1.setProfessional(p1);
        user1.setPersonType(User.PersonType.FISICA);

        // bidirecional reference
        p1.setWallet(cw1);
        cw1.setProfessional(p1);


        ////////////////////////////////////////
        User user2 = new User("Diego", "123qwe", "Diego@bol");

        Address address2 = new Address();
        address2.setLatitude("-22.750996");
        address2.setLongitude("-43.45973010000001");


        Professional s2 = new Professional();
        s2.setNameProfessional("Diego");
        s2.setAddress(address2);
        s2.setUser(user2);
        s2.setDateRegister(Timestamp.valueOf(now()));
        s2.setBoss(p1);
        p1.getEmployeesCollection().add(s2);


        user2.setProfessional(s2);
        user2.setPersonType(User.PersonType.FISICA);
        address2.setProfessional(s2);



        ////////////////////////////////////////
        User user3;

        Address address3 = new Address();
        address3.setLatitude("-22.7248375");
        address3.setLongitude("-43.5251476");

        Professional s3 = new Professional();
        s3.setNameProfessional("Deivison");
        s3.setDateRegister(Timestamp.valueOf(now()));
        s3.setBoss(p1);
        p1.getEmployeesCollection().add(s3);

        s3.setAddress(address3);
        address3.setProfessional(s3);

        s3.setUser(user3 = new User("Deivison", "123qwe", "Deivison@bol"));
        user3.setProfessional(s3);
        user3.setPersonType(User.PersonType.JURIDICA);

        ////////////////////////////////////////
        User user4;
        Address address4 = new Address();
        address4.setLatitude("-22.9111");
        address4.setLongitude("-43.1826");

        Professional s4 = new Professional();
        s4.setNameProfessional("Vinicius");
        s4.setDateRegister(Timestamp.valueOf(now()));

        s4.setAddress(address4);
        address4.setProfessional(s4);

        s4.setUser(user4 = new User("Vinicius", "123qwe", "Vinicius@bol"));
        user4.setProfessional(s4);
        user4.setPersonType(User.PersonType.FISICA);

        ////////////////////////////////////////
        User user5;
        Address address5 = new Address();
        address5.setLatitude("-22.7269425");
        address5.setLongitude("-43.528198");

        Professional s5 = new Professional();
        s5.setStatus(Professional.Status.ACTIVE);
        s5.setNameProfessional("Habib");
        s5.setDateRegister(Timestamp.valueOf(now()));

        s5.setAddress(address5);
        address5.setProfessional(s5);

        s5.setUser(user5 = new User("Habib", "123qwe", "Habib@bol"));
        user5.setProfessional(s5);
        user5.setPersonType(User.PersonType.JURIDICA);

        repository.save(p1);
        repository.save(s2);
        repository.save(s3);
        repository.save(s4);
        repository.save(s5);

        //SEGUE ABAIXO O ITEM 1 DO CARD 39
        //Alterar ProfessionalPreLoadConfiguration, de modo a incluir + 2 profissionais
        //com endereços e Locations fictícios.

        User user6 = new User("kelly", "123abc", "joana@bol");

        Address address6 = new Address();
        address6.setAddress("Travessa Tuviassuiara, 32");
        address6.setNeighborhood("Rodilândia");
        address6.setCity("Nova Iguaçu");
        address6.setState("Rio de Janeiro");
        address6.setCountry("Brazil");
        address6.setCep("26083-285");

        Professional s6 = new Professional();
        s6.setNameProfessional("Kelly");
        s6.setAddress(address6);
        s6.setUser(user6);
        s6.setDateRegister(Timestamp.valueOf(now()));


        address6.setProfessional(s6);
        user6.setProfessional(s6);
        user6.setPersonType(User.PersonType.JURIDICA);

        repository.save(s6);

        User user7 = new User("kdoba", "123abc", "joao@bol");
        Address address7 = new Address();
        address7.setAddress("Avenida Marechal Floriano, 46");
        address7.setNeighborhood("Centro ");
        address7.setCity("Rio de Janeiro/");
        address7.setState("Rio de Janeiro");
        address7.setCountry("Brazil");
        address7.setCep("20080-001");

        Professional s7 = new Professional();
        s7.setNameProfessional("Kdoba");
        s7.setAddress(address7);
        s7.setDateRegister(Timestamp.valueOf(now()));

        s7.setUser(user7);

        address7.setProfessional(s7);
        user7.setProfessional(s7);
        user7.setPersonType(User.PersonType.FISICA);

        repository.save(s7);

        //ADICIONADO NOVO PROFESSIONAL COM EMAIL CORRETO PARA TESTAR CORRETAMENTE NO APP
        User user8 = new User("emailOk", "123abc", "ok@email.com");
        Address address8 = new Address();
        address8.setAddress("Rua José Paulino, 152");
        address8.setNeighborhood("Rodilândia ");
        address8.setCity("Nova Iguaçu");
        address8.setState("Rio de Janeiro");
        address8.setCountry("Brazil");
        address8.setCep("26083-485");

        Professional s8 = new Professional();
        s8.setNameProfessional("emailOK");
        s8.setAddress(address8);
        s8.setDateRegister(Timestamp.valueOf(now()));

        s8.setUser(user8);

        address8.setProfessional(s8);
        user8.setProfessional(s8);
        user8.setPersonType(User.PersonType.FISICA);

        repository.save(s8);

        //ADICIONADO PARA TESTAR PELO POSTMAN O CARD RNF76
        Professional professional = createFakeProfessional();
        professional.getUser().setUsername("testPaymentPreload-professional");
        professional.getUser().setEmail("testPaymentPreload-professional@email.com");
        professional.getUser().setPassword("123");
        professional.setCnpj("098.605.789-06");

        professionalRepository.save(professional);

        Category category = categoryRepository.findByName("MASSOTERAPEUTA");

        if(category == null) {
            category = new Category();
            category.setName("MASSOTERAPEUTA");
            categoryRepository.save(category);
        }

        ProfessionalCategory ps1 = new ProfessionalCategory(professional, category);

        professional.getProfessionalCategoryCollection().add(ps1);

        // Atualizando associando o Profeissional ao Servico
        professionalRepository.save(professional);

    }

    public static Professional createFakeProfessional() {
        Professional c1 = new Professional();
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCnpj("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameProfessional("João da Silva");
        //c1.setOrderCollection(null);
        c1.setStatus(Professional.Status.ACTIVE);
        c1.setAddress(createFakeAddress());
        c1.setUser(createFakeUser("222", "222@2.com"));
        c1.getUser().setProfessional(c1);
        c1.getUser().setPersonType(User.PersonType.JURIDICA);

        return c1;
    }

    public static User createFakeUser(String username, String email) {
        User u = new User();
        u.setEmail(email);
        //u.setUser(1234L);
        u.setPassword("123qwe");
        u.setSourceApp("google+");
        u.setUsername(username);
        //u.getCustomerCollection().add(c);
        //userRepository.save(u);
        return u;
    }

    static Address createFakeAddress() {
        Address a = new Address();
        a.setAddress("Rua Perlita");
        a.setCep("0000000");
        a.setCity("RJO");
        a.setCountry("BRA");
        a.setNeighborhood("Austin");
        a.setState("RJ");

        return a;
    }

}
