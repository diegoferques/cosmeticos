package com.cosmeticos.controller;

import com.cosmeticos.model.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

class OrderJsonHelper {

    public static String buildJsonCreateNonScheduledOrder(
            Customer customer,
            ProfessionalCategory professionalCategory,
            Payment.Type paymentType,
            PriceRule priceRule) {

        return "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : "+ Timestamp.valueOf(LocalDateTime.now()).getTime()+",\n" +
                "    \"status\" : 0,\n" +

                //"    \"scheduleId\" : {\n" +
                //"      \"scheduleStart\" : \""+ scheduleStart +"\"\n" +
                //"    },\n" +

                "    \"professionalCategory\" : {\n" +
                "      \"professionalCategoryId\": " +professionalCategory.getProfessionalCategoryId()+ "\n" +
                "    },\n" +

                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : "+ customer.getIdCustomer() +"\n" +
                "    },\n" +

                "    \"paymentCollection\" : \n" +
                "    [\n" +
                "       {\n" +
                "         \"type\": \""+paymentType.toString()+"\",\n" +
                "         \"parcelas\": 1,\n" +
                "         \"priceRule\": {\n" +
                "             \"id\": " + priceRule.getId() + "\n" +
                "         }\n" +
                "       }\n" +
                "    ]\n" +

                "  }\n" +
                "}";
    }

    public static String buildJsonCreateScheduledOrder(Customer customer,
                                                       ProfessionalCategory professionalCategory,
                                                       PriceRule priceRule,
                                                       Payment.Type paymentType,
                                                       Long scheduleStart) {
        return "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : "+Timestamp.valueOf(LocalDateTime.now()).getTime()+",\n" +
                "    \"status\" : 0,\n" +

                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \""+ scheduleStart +"\"\n" +
                "    },\n" +

                "    \"professionalCategory\" : {\n" +
                "      \"professionalCategoryId\": " +professionalCategory.getProfessionalCategoryId()+ "\n" +
                "    },\n" +

                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : "+ customer.getIdCustomer() +"\n" +
                "    },\n" +

                "    \"paymentCollection\" : \n" +
                "    [\n" +
                "       {\n" +
                "         \"type\": \""+paymentType.toString()+"\",\n" +
                "         \"parcelas\": 1,\n" +
                "         \"priceRule\": {\n" +
                "             \"id\": " + priceRule.getId() + "\n" +
                "         }\n" +
                "       }\n" +
                "    ]\n" +

                "  }\n" +
                "}";
    }

    public static String buildJsonUpdateScheduledOrder(Long idOrder,
                                                       Order.Status orderStatus,
                                                       Long scheduleId,
                                                       Long scheduleEnd) {
        return "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : "+idOrder+",\n" +
                "    \"status\" : "+orderStatus.ordinal()+",\n" +

                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\": "+scheduleId+",\n" +
                "      \"scheduleEnd\" : \""+ scheduleEnd +"\"\n" +
                "    }\n" +

                "  }\n" +
                "}";
    }

}
