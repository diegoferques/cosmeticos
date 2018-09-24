package com.cosmeticos.jsonizer;

import com.cosmeticos.commons.*;
import com.cosmeticos.controller.CustomerControllerTests;
import com.cosmeticos.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Ignore;
import org.junit.Test;

import java.lang.Exception;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by Lulu on 30/05/2017.
 */
@Ignore
public class JsonizerTest222 {
    ObjectMapper om = new ObjectMapper();

    @Test
    public void jsonizeRole() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Role r = Role.builder().name("qualquer coisa").build();

        RoleRequestBody body = new RoleRequestBody();
        body.setEntity(r);

        String json = om.writeValueAsString(body);

        System.out.println(json);
    }

    @Test
    public void jsonizeUser() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);


        CreditCard cc = CreditCard.builder().build();
        cc.setToken("1234");
        cc.setVendor("MasterCard");
        cc.setStatus(CreditCard.Status.ACTIVE);

        //User
        User u1 = new User();
        u1.getCreditCardCollection().add(cc);
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

        Customer customer = CustomerControllerTests.createFakeCustomer();

        CustomerRequestBody requestBody = new CustomerRequestBody();
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
        c1.setUser(new User("profissional1@gmail.con", "123qwe"));
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

        Category s = new Category();
        s.setName("s");

        Professional p = new Professional();
        p.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
        p.setCellPhone("(21) 99887-7665");
        p.setDateRegister(Calendar.getInstance().getTime());
        p.setGenre('F');
        p.setNameProfessional("Fernanda Cavalcante");
        p.setStatus(Professional.Status.INACTIVE);

        Hability h = new Hability();
        h.setName("h");
        h.getProfessionalCollection().add(p);

        HabilityRequestBody requestBody = new HabilityRequestBody();
        requestBody.setHability(h);

        String json = om.writeValueAsString(requestBody);

        System.out.println(json);

    }

    @Test
    public void jsonizeService() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Category r= new Category();
        r.setIdCategory((long)1);

        String json = om.writeValueAsString(r);

        System.out.println(json);
    }

    @Test
    public void jsonizeScheduleRequestBody() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        ScheduleRequestBody scheduleRequest = new ScheduleRequestBody();
        scheduleRequest.setScheduleStart(Calendar.getInstance().getTime());
        scheduleRequest.setIdCustomer(1L);
        scheduleRequest.setIdProfessional(1L);

        String json = om.writeValueAsString(scheduleRequest);

        System.out.println(json);
    }

    @Test
    public void jsonizeBossEmployees() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Professional boss = new Professional();
        boss.setNameProfessional("boss");

        Professional employee1 = new Professional();
        employee1.setNameProfessional("employee1");

        Professional employee2 = new Professional();
        employee2.setNameProfessional("employee2");

        boss.getEmployeesCollection().add(employee1);
        boss.getEmployeesCollection().add(employee2);

        String json = om.writeValueAsString(boss);

        System.out.println(json);
    }

    @Test
    public void justPrintJson()
    {


        System.out.println("{\n" +
                        "  \"professional\": {\n" +
                        "    \"idProfessional\": \"" + 1 + "\",\n" +
                        "        \"priceRule\": [\n" +
                        "          {\n" +
                        "            \"name\": \"COMPRIMENTO ATÉ 30cm\",\n" +
                        "            \"price\": 80.00\n" +
                        "          }\n" +
                        "          ]\n" +
                        "  }\n" +
                        "}");
    }
}
