package com.cosmeticos.service;

import com.cosmeticos.Application;
import com.cosmeticos.model.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.cosmeticos.model.Order.Status.OPEN;
import static org.springframework.http.HttpMethod.POST;

/**
 * Identifica qquem esta fazendo a requisicao (cliente ou profissional) e:
 * - se for cliente: manda push pro profissional
 * - se for profissional: manda push pro cliente.
 */
@Slf4j
@Service
public class FirebasePushNotifierService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private Environment env;

    private String pathToFcm = "https://fcm.googleapis.com/fcm/send";
    private String serverKey = "AIzaSyAytvPfzrG3jaNIxQPY-5dn2phBl4fz3Wg";

    public void push(Order persistentOrder) {

        Object requestorSessionValue = httpSession.getAttribute(Application.PRINCIPAL_EMAIL_HEADER_KEY);

        if (requestorSessionValue != null) {

            String principalEmail = String.valueOf(requestorSessionValue);

            User professionalUser = persistentOrder.getProfessionalCategory()
                    .getProfessional()
                    .getUser();
            User customerUser= persistentOrder.getIdCustomer()
                    .getUser();

            if(principalEmail.equalsIgnoreCase(professionalUser.getEmail()))
            {
                pushCustomer(persistentOrder);
            }
            else if(principalEmail.equalsIgnoreCase(customerUser.getEmail()))
            {
                pushProfessional(persistentOrder);
            }
            else
            {
                log.warn("Email recebido nao corresponde a cliente e profissional envolvidos no pedido: {}", principalEmail);
            }
        } else {
            log.warn("Nao ha header de usuario 'princpal.email' configurado na sessao.");
        }
    }

    private void push(Map<String, Object> payloadNotificationObj, Map<String, Object> dataObj, User destinationUser) {

        if(destinationUser.getFirebaseInstanceId() != null) {

            Map<String, Object> payloadRootObj = new HashMap() {{
                put("to", destinationUser.getFirebaseInstanceId());
                put("data", dataObj);
                put("notification", payloadNotificationObj);
            }};

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.set("Authorization", "key=" + serverKey);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payloadRootObj, httpHeaders);

            StopWatch  stopWatch = new StopWatch();
            stopWatch.start();
            try {

                ResponseEntity<String> exchange = restTemplate.exchange(new URI(pathToFcm), POST, httpEntity, String.class);
                MDC.put("firebasePushStatus", String.valueOf(exchange.getStatusCode()));
            } catch (HttpClientErrorException e) {
                log.warn("Erro ao notificar Firebase", e);
            } catch (URISyntaxException e) {
                log.warn("Erro ao notificar Firebase", e);
            }
            finally {
                MDC.put("firebasePushIntegrationTime", String.valueOf(stopWatch.getTotalTimeMillis()));
            }
        }
        else
        {
            log.warn("Usuario {} nao possui token de aplicativo do firebase e nao recebera push notification: ", destinationUser.getEmail());
        }
    }

    private void pushCustomer(Order order) {

        Customer customer = order.getIdCustomer();

        Professional professional = order.getProfessionalCategory().getProfessional();

        String orderStatusTranslation = env.getProperty(order.getStatus().getTranslationPropertyKey());

        HashMap dataObj = new HashMap() {{
            put("orderId", order.getIdOrder());
            put("orderStatus", order.getStatus().name());
            put("orderCounterpartStakeholder", professional.getNameProfessional());
        }};

        HashMap notificationObj = new HashMap() {{
            put("title", "Seu pedido Cod " + order.getIdOrder() + " foi atualizado por "+professional.getNameProfessional()+"!");
            put("body", orderStatusTranslation);
        }};

        push(notificationObj, dataObj, customer.getUser());
    }

    private void pushProfessional(Order order) {

        Customer customer = order.getIdCustomer();

        Professional professional = order.getProfessionalCategory().getProfessional();

        Category category = order.getProfessionalCategory().getCategory();

        String orderStatusTranslation = env.getProperty(order.getStatus().getTranslationPropertyKey());

        HashMap dataObj = new HashMap() {{
            put("orderId", order.getIdOrder());
            put("orderStatus", order.getStatus().name());
            put("orderCounterpartStakeholder", customer.getNameCustomer());
        }};

        final String title =
                (OPEN.equals(order.getStatus()))
                        ?
                "NOVO PEDIDO: " + category.getName() + " - Cod " + order.getIdOrder() + ", de "+customer.getNameCustomer()
                :
                "Seu pedido Cod " + order.getIdOrder() + " foi atualizado por "+customer.getNameCustomer()+"!";

        HashMap notificationObj = new HashMap() {{
            put("title", title);
            put("body", orderStatusTranslation);
        }};

        push(notificationObj, dataObj, professional.getUser());
    }
}
