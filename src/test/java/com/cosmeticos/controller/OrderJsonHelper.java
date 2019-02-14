package com.cosmeticos.controller;

import com.cosmeticos.model.*;
import com.cosmeticos.service.RandomCode;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

class OrderJsonHelper {

    /**
     * @param customer
     * @param professionalCategory
     * @param paymentType
     * @param oneClick             Determina se sera criado um json pra oneclick
     * @param oneClickSave         Determina se sera criado um json pra oneclick mas sendo o primeiro envio de cartao do cliente.
     * @param priceRule
     * @return
     */
    public static String buildJsonCreateNonScheduledOrder(
            Customer customer,
            ProfessionalCategory professionalCategory,
            Payment.Type paymentType,
            boolean oneClick,
            boolean oneClickSave,
            PriceRule priceRule) {

        String cc = "";
        if (paymentType.equals(Payment.Type.CC)) {

            if (oneClickSave || !oneClick) {
                cc = "         \"creditCard\": {\n" +
                        "\t\t        \"token\": \"ALTERADOOOOOOOOOOOOO\",\n" +
                        "\t\t        \"ownerName\": \"Teste\",\n" +
                        "\t\t        \"suffix\": \"" + new RandomCode(4).nextString() + "\",\n" +
                        "\t\t        \"securityCode\": \"098\",\n" +
                        "\t\t        \"expirationDate\": \"" + Timestamp.valueOf(now().plusDays(30)).getTime() + "\",\n" +
                        "\t\t        \"vendor\": \"MasterCard\",\n" +
                        "\t\t        \"oneClick\": \""+ oneClickSave +"\",\n" +
                        "\t\t        \"status\": \"ACTIVE\"\n" +
                        "\t\t     },\n";
            }
        }

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


                cc +


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

    public static String buildJsonCreateNonScheduledOrder(
            Customer customer,
            ProfessionalCategory professionalCategory,
            Payment.Type paymentType,
            PriceRule priceRule) {

        // Faz pagamento avulso de cartao. Sem salvar pra oneclick
        return buildJsonCreateNonScheduledOrder(
                customer,
                professionalCategory,
                paymentType,
                false,
                true,
                priceRule
        );
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

        //String cc = "         \"creditCard\": null,\n";
        String cc = "";
        if (paymentType.equals(Payment.Type.CC)) {

            if (oneClickSave || !oneClick) {
                cc = "         \"creditCard\": {\n" +
                        "\t\t        \"token\": \"ALTERADOOOOOOOOOOOOO\",\n" +
                        "\t\t        \"ownerName\": \"Teste\",\n" +
                        "\t\t        \"suffix\": \"" + new RandomCode(4).nextString() + "\",\n" +
                        "\t\t        \"securityCode\": \"098\",\n" +
                        "\t\t        \"expirationDate\": \"" + Timestamp.valueOf(now().plusDays(30)).getTime() + "\",\n" +
                        "\t\t        \"vendor\": \"MasterCard\",\n" +
                        "\t\t        \"status\": \"ACTIVE\",\n" +
                        "\t\t        \"oneClick\": " + oneClick + "\n" +
                        "\t\t     },\n";
            }
        }

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


                cc +


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
