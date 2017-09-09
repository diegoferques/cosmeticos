package com.cosmeticos.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Created by Vinicius on 08/09/2017.
 */
@Component
@lombok.extern.slf4j.Slf4j
public class SuperpayCompletoClient {

    @Qualifier("webserviceTemplateOneClick")
    private WebServiceTemplate webserviceTemplate;

}
