package com.cosmeticos.payment.cielo;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.payment.cielo.model.*;
import com.cosmeticos.validation.OrderException;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

import static feign.FeignException.errorStatus;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@FeignClient(value = "cieloTransactionClient", url = "${creditcard.gw.transaction.url}")
public interface CieloTransactionClient {

    /**
     *
     * @param requestId Guid 	Opcional 	Identificador do Request, utilizado quando o lojista usa diferentes servidores para cada GET/POST/PUT.
     * @param authorizeRequest
     * @return
     */
    @RequestMapping(
            method = PUT,
            //value = "/1/sales",
            value = "/5ae739fa2f00007600f05a01",
            headers = {

                    /**
                     * Guid     Obrigatorio   Identificador da loja na Cielo.
                     */
                    "merchantId=${creditcard.gw.merchantid}",

                    /**
                     * Texto 	Obrigatorio 	Chave Publica para Autenticação Dupla na Cielo.
                     */
                    "merchantKey=${creditcard.gw.merchantKey}"
            }
    )
    AuthorizeAndTokenResponse reserve(
            @RequestHeader(value = "requestId", required = false) String requestId,
            AuthorizeAndTokenRequest authorizeRequest
    );


    @RequestMapping(
            method = PUT,
            //value = "/1/sales/{PaymentId}/capture",
            value = "/5ae772ed2f00001000f05a9a",
            headers = {

                    /**
                     * Guid     Obrigatorio   Identificador da loja na Cielo.
                     */
                    "merchantId=${creditcard.gw.merchantid}",

                    /**
                     * Texto 	Obrigatorio 	Chave Publica para Autenticação Dupla na Cielo.
                     */
                    "merchantKey=${creditcard.gw.merchantKey}"
            }
    )
    CaptureResponse capture(
            @RequestHeader(value = "requestId", required = false) String requestId,
            @PathVariable(value = "PaymentId") String paymentId
    );


    @Slf4j
    class IprettyErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {

            try {
                CieloApiErrorResponseBody cieloResponse = readResponse(response);

                CieloApiErrorCode cieloApiErrorCode = CieloApiErrorCode.from(cieloResponse.getCode());

                return new OrderException(cieloApiErrorCode, cieloResponse, "Payment Failure");
            } catch (IOException e) {
                return new OrderValidationException(ResponseCode.GATEWAY_FAILURE, e);
            }
            //return errorStatus(methodKey, response);
        }

        private CieloApiErrorResponseBody readResponse(Response response) throws IOException {
            byte[] body = response.request().body();
            List<CieloApiErrorResponseBody> responseBodies = new ObjectMapper().readValue(body, List.class);

            return responseBodies.get(0);
        }
    }
}
