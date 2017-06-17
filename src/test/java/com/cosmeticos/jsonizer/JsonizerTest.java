package com.cosmeticos.jsonizer;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Role;
import com.cosmeticos.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;

/**
 * Created by Lulu on 30/05/2017.
 */
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



    private Customer createFakeCustomer() {
        Customer c4 = new Customer();
        c4.setBirthDate(Timestamp.valueOf(LocalDateTime.of(1991, 10, 21, 0, 0, 0)));
        c4.setCellPhone("(21) 99887-7665");
        c4.setCpf("816.810.695-68");
        c4.setDateRegister(Calendar.getInstance().getTime());
        c4.setGenre('F');
        c4.setNameCustomer("Fernanda Cavalcante");
        c4.setOrderCollection(null);
        c4.setStatus(Customer.Status.INACTIVE.ordinal());
        return c4;
    }
}
