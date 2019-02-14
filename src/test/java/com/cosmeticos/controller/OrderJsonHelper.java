package com.cosmeticos.controller;

import com.cosmeticos.model.*;
import com.cosmeticos.service.RandomCode;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

class OrderJsonHelper {


    public static String buildJsonCreateNonScheduledOrder(
            Customer customer,
            ProfessionalCategory professionalCategory,
            Payment.Type paymentType,
            PriceRule priceRule) {

        // Faz pagamento avulso de cartao. Sem salvar pra oneclick
        return "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : " + Timestamp.valueOf(LocalDateTime.now()).getTime() + ",\n" +
                "    \"status\" : 0,\n" +
                "    \"attendanceType\": \"ON_SITE\"," +

                //"    \"scheduleId\" : {\n" +
                //"      \"scheduleStart\" : \""+ scheduleStart +"\"\n" +
                //"    },\n" +

                "    \"professionalCategory\" : {\n" +
                "      \"professionalCategoryId\": " + professionalCategory.getProfessionalCategoryId() + "\n" +
                "    },\n" +

                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : " + customer.getIdCustomer() + "\n" +
                "    },\n" +

                "    \"paymentCollection\" : \n" +
                "    [\n" +
                "       {\n" +

                "         \"type\": \"" + paymentType.toString() + "\",\n" +
                "         \"parcelas\": 1,\n" +
                "         \"priceRule\": {\n" +
                "             \"id\": " + priceRule.getId() + "\n" +
                "         }\n" +
                "       }\n" +
                "    ]\n" +

                "  }\n" +
                "}";
    }

    /**
     * @param customer
     * @param professionalCategory
     * @param priceRule
     * @param paymentType
     * @param oneClick             Determina se sera criado um json pra oneclick
     * @param oneClickSave         Determina se sera criado um json pra oneclick mas sendo o primeiro envio de cartao do cliente.
     * @param scheduleStart
     * @return
     */
    public static String buildJsonCreateScheduledOrder(Customer customer,
                                                       ProfessionalCategory professionalCategory,
                                                       PriceRule priceRule,
                                                       Payment.Type paymentType,
                                                       boolean oneClick,
                                                       boolean oneClickSave,
                                                       Long scheduleStart) {
        return "{\n" +
                "  \"order\" : {\n" +
                "    \"date\" : " + Timestamp.valueOf(LocalDateTime.now()).getTime() + ",\n" +
                "    \"status\" : 0,\n" +
                "    \"attendanceType\": \"ON_SITE\"," +

                "    \"scheduleId\" : {\n" +
                "      \"scheduleStart\" : \"" + scheduleStart + "\"\n" +
                "    },\n" +

                "    \"professionalCategory\" : {\n" +
                "      \"professionalCategoryId\": " + professionalCategory.getProfessionalCategoryId() + "\n" +
                "    },\n" +

                "    \"idCustomer\" : {\n" +
                "      \"idCustomer\" : " + customer.getIdCustomer() + "\n" +
                "    },\n" +

                "    \"paymentCollection\" : \n" +
                "    [\n" +
                "       {\n" +

                "         \"type\": \"" + paymentType.toString() + "\",\n" +
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

        // Faz pagamento avulso de cartao. Sem salvar pra oneclick
        return buildJsonCreateScheduledOrder(
                customer,
                professionalCategory,
                priceRule,
                paymentType,
                false,
                false,
                scheduleStart
        );
    }

    public static String buildJsonUpdateScheduledOrder(Long idOrder,
                                                       Order.Status orderStatus,
                                                       Long scheduleId,
                                                       Long scheduleEnd) {
        return "{\n" +
                "  \"order\" : {\n" +
                "    \"idOrder\" : " + idOrder + ",\n" +
                "    \"status\" : " + orderStatus.ordinal() + ",\n" +
                "    \"attendanceType\": \"ON_SITE\"," +

                "    \"scheduleId\" : {\n" +
                "      \"scheduleId\": " + scheduleId + ",\n" +
                "      \"scheduleEnd\" : \"" + scheduleEnd + "\"\n" +
                "    }\n" +

                "  }\n" +
                "}";
    }

}
