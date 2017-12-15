package com.cosmeticos.controller;

import com.cosmeticos.model.CreditCard;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Utility {
    public static String toJson(Object cc) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();

        om.enable(SerializationFeature.INDENT_OUTPUT);

        String json = om.writeValueAsString(cc);

        System.out.println(json);

        return json;
    }
}
