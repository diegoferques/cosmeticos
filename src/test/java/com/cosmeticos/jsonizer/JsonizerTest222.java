package com.cosmeticos.jsonizer;

import com.cosmeticos.commons.*;
import com.cosmeticos.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by Lulu on 30/05/2017.
 */
public class JsonizerTest222 {
    ObjectMapper om = new ObjectMapper();

    @Test
    public void jsonizeRole() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Role r= new Role();
        r.setName("qualquer coisa");

        RoleRequestBody body = new RoleRequestBody();
        body.setEntity(r);

        String json = om.writeValueAsString(body);

        System.out.println(json);
    }

    @Test
    public void jsonizeUser() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);


        CreditCard cc = new CreditCard();
        cc.setToken("1234");
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);

        //User
        User u1 = new User();
        u1.getCreditCardCollection().add(cc);
        u1.setUsername("KILLER");
        u1.setPassword("109809876");
        u1.setEmail("Killer@gmail.com");
        u1.setSourceApp("facebook");

        UserRequestBody body = new UserRequestBody();
        body.setEntity(u1);

        String json = om.writeValueAsString(body);

        System.out.println(json);
    }

    @Test
    public void jsoniseCustomer() throws JsonProcessingException {

        om.enable(SerializationFeature.INDENT_OUTPUT);

        Customer customer = createFakeCustomer();
        Address addres = new Address();
        User user = new User();

        CustomerRequestBody requestBody = new CustomerRequestBody();
        requestBody.setAddress(addres);
        requestBody.setUser(user);
        requestBody.setCustomer(customer);

        String json = om.writeValueAsString(requestBody);

        System.out.println(json);
    }

    @Test
    public void jsonizeProfessional() throws JsonProcessingException {
        om.enable(SerializationFeature.INDENT_OUTPUT);


        Professional c1 = new Professional();
        c1.setBirthDate(Timestamp.valueOf(LocalDateTime.MAX.of(1980, 01, 20, 0, 0, 0)));
        c1.setCellPhone("(21) 98877-6655");
        c1.setCnpj("098.765.432-10");
        c1.setDateRegister(Calendar.getInstance().getTime());
        c1.setGenre('M');
        c1.setNameProfessional("João da Silva");
        c1.setStatus(Professional.Status.ACTIVE);
        c1.setAddress(new Address());
        c1.setUser(new User("profissional1", "123qwe", "profissional1@gmail.con"));
        c1.setHabilityCollection(new HashSet<Hability>(){{
            add(new Hability("Escova Progressiva"));
            add(new Hability("Relaxamento"));
            add(new Hability("Nova Habilidade"));
        }});

        ProfessionalRequestBody requestBody = new ProfessionalRequestBody();
        requestBody.setProfessional(c1);


        String json = om.writeValueAsString(requestBody);
        System.out.println(json);
    }

    @Test
    public void jsonizeHability() throws JsonProcessingException {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Service s = new Service();
        s.setCategory("s");

        Professional p = new Professional();
        p.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
        p.setCellPhone("(21) 99887-7665");
        p.setDateRegister(Calendar.getInstance().getTime());
        p.setGenre('F');
        p.setNameProfessional("Fernanda Cavalcante");
        p.setStatus(Professional.Status.INACTIVE);

        Hability h = new Hability();
        h.setName("h");
        h.setService(s);
        h.getProfessionalCollection().add(p);

        HabilityRequestBody requestBody = new HabilityRequestBody();
        requestBody.setHability(h);

        String json = om.writeValueAsString(requestBody);

        System.out.println(json);

    }

    @Test
    public void jsonizeService() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Service r= new Service();
        r.setIdService((long)1);

        String json = om.writeValueAsString(r);

        System.out.println(json);
    }

    @Test
    public void jsonizeScheduleRequestBody() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        ScheduleRequestBody scheduleRequest = new ScheduleRequestBody();
        scheduleRequest.setScheduleDate(Calendar.getInstance().getTime());
        scheduleRequest.setIdCustomer(1L);
        scheduleRequest.setIdProfessional(1L);
        scheduleRequest.setIdService(1L);

        String json = om.writeValueAsString(scheduleRequest);

        System.out.println(json);
    }

    public static void main(String args[]) throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);

        //User
        User u1 = new User();
        u1.setUsername("KILLER");
        u1.setPassword("109809876");
        u1.setEmail("Killer@gmail.com");
        u1.setSourceApp("facebook");
        Customer c1 = createFakeCustomer();
        c1.setIdLogin(u1);

        Professional p = new Professional();
        p.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
        p.setCellPhone("(21) 99887-7665");
        p.setDateRegister(Calendar.getInstance().getTime());
        p.setGenre('F');
        p.setNameProfessional("Fernanda Cavalcante");
        p.setStatus(Professional.Status.INACTIVE);

        Service srv1 = new Service();
        srv1.setCategory("MASSAGISTA");

        ProfessionalServices ps1 = new ProfessionalServices();
        ps1.setProfessional(p);
        ps1.setService(srv1);

        p.getProfessionalServicesCollection().add(ps1);

        //Schedule s1 = scheduleRepository.findOne(1L);
        Schedule s1 = new Schedule();
        s1.setScheduleDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 07, 10, 14, 00, 0)));
        s1.setStatus(Schedule.Status.ACTIVE);

        Sale o1 = new Sale();
        o1.setStatus(Sale.Status.CREATED.ordinal());
        o1.setDate(Timestamp.valueOf(LocalDateTime.MAX.of(2017, 06, 24, 14, 10, 0)));
        o1.setIdCustomer(c1);
        //o1.setIdLocation();
        o1.setScheduleId(s1);

        OrderRequestBody or = new OrderRequestBody();
        or.setSale(o1);

        String json = om.writeValueAsString(or);
        System.out.println(json);
    }

    private static Customer createFakeCustomer() {
        Customer c4 = new Customer();
        c4.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
        c4.setCellPhone("(21) 99887-7665");
        c4.setCpf("816.810.695-68");
        c4.setDateRegister(Calendar.getInstance().getTime());
        c4.setGenre('F');
        c4.setNameCustomer("Fernanda Cavalcante");
        c4.setSaleCollection(null);
        c4.setStatus(Customer.Status.INACTIVE.ordinal());
        return c4;
    }
}
