package com.cosmeticos;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by Lulu on 30/05/2017.
 */
public class JsonizerTest222 {
    ObjectMapper om = new ObjectMapper();

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
