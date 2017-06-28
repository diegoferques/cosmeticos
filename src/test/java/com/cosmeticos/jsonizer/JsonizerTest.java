package com.cosmeticos.jsonizer;

import com.cosmeticos.commons.*;

import com.cosmeticos.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

/**
 * Created by Lulu on 30/05/2017.
 */
@Ignore
public class JsonizerTest {
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

    private Customer createFakeCustomer() {
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
