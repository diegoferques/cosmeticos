package com.cosmeticos.cielo;

import com.cosmeticos.cielo.model.AuthorizeAndTokenRequest;
import com.cosmeticos.cielo.model.AuthorizeAndTokenResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

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
            value = "/1/sales",
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
    AuthorizeAndTokenResponse authorize(
            @RequestHeader(value = "requestId", required = false) String requestId,
            AuthorizeAndTokenRequest authorizeRequest
    );
}
