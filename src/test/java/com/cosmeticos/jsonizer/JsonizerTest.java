package com.cosmeticos.jsonizer;

import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.Role;
import com.cosmeticos.model.Service;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

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
    public void jsonizeProfessional() throws Exception {
        om.enable(SerializationFeature.INDENT_OUTPUT);

        Professional r= new Professional();
        r.setIdProfessional((long)1);

        String json = om.writeValueAsString(r);

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
}
