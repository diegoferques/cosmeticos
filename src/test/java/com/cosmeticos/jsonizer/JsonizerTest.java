package com.cosmeticos.jsonizer;

import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.model.Role;
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
}
