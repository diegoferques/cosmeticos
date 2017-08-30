package com.cosmeticos.service;

import com.cosmeticos.payment.superpay.client.rest.model.RetornoTransacao;
import com.cosmeticos.payment.superpay.client.rest.model.Usuario;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Service
public class PaymentService {

    @Value("${superpay.url.transacao}")
    private String urlTransacao;

    @Value("${superpay.estabelecimento}")
    private String estabelecimento;

    @Value("${superpay.login}")
    private String login;

    @Value("${superpay.senha}")
    private String senha;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;


    //TODO - CRIAR AS ROTINAS PARA SALVAR NO BANCO O QUE FOR NECESSARIO DO RETORNO DO PAGAMENTO NO GATEWAY DE PAGAMENTO

    //TODO - TEMOS QUE DEFINIR LOGO SE VAMOS USAR ORDER ID PARA CRIAR OS PEDIDOS NA SUPERPAY OU SE VAMOS USAR PAYMENT ID
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/118
    public ResponseEntity<RetornoTransacao> capturaTransacaoSuperpay(Long numeroTransacao) throws JsonProcessingException, URISyntaxException {

        RestTemplate restTemplate = restTemplateBuilder.build();

        String urlCapturaTransacao = urlTransacao + "/" + estabelecimento + "/" + numeroTransacao + "/capturar";

        ObjectMapper om = new ObjectMapper();
        String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

            RequestEntity<RetornoTransacao> entity = RequestEntity
                    .post(new URI(urlCapturaTransacao))
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("usuario", jsonHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(null);

            ResponseEntity<RetornoTransacao> exchange = doCapturaTransacaoRequest(restTemplate, entity);

        if(exchange.getStatusCode() == HttpStatus.CONFLICT) {
            log.warn("Conflitou");
        }

        return exchange;
    }

    public ResponseEntity<RetornoTransacao> doConsultaTransacaoRequest(RestTemplate restTemplate,
                RequestEntity<RetornoTransacao> entity) {
        return restTemplate.exchange(entity, RetornoTransacao.class);
    }

    public ResponseEntity<RetornoTransacao> doCapturaTransacaoRequest(RestTemplate restTemplate, RequestEntity<RetornoTransacao> entity) {
        return restTemplate.exchange(entity, RetornoTransacao.class);
    }

    //TODO - VERIFICAR NO GATEWAY SUPERPAY SE HOUVE UMA ATUALIZACAO NA TRANSACAO
    //ESTE METODO SERA RESPONSAVEL PELA ATUALZICAO DO STATUS DO PAGAMENTO, QUE AINDA DEVERA SER IMPLEMENTADO
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/129
    public Optional<RetornoTransacao> consultaTransacao(Long numeroTransacao) throws JsonProcessingException {

        RestTemplate restTemplate = restTemplateBuilder.build();

        String urlConsultaTransacao = urlTransacao + "/" + estabelecimento + "/" + numeroTransacao;

        RetornoTransacao retornoTransacao;

        ObjectMapper om = new ObjectMapper();
        String jsonHeader = om.writeValueAsString(new Usuario(login, senha));

        HttpHeaders headers = new HttpHeaders();
        headers.set("usuario", jsonHeader);

        HttpEntity entity = new HttpEntity(headers);

            ResponseEntity<RetornoTransacao> exchange = doConsultaTransacao(restTemplate, urlConsultaTransacao, entity);

        if(exchange.getStatusCode() == HttpStatus.OK) {
            retornoTransacao = exchange.getBody();

        } else {
            retornoTransacao = null;

        }

        if(exchange.getStatusCode() == HttpStatus.CONFLICT){
            log.warn("Conflitou");
        }

        return Optional.ofNullable(retornoTransacao);
    }

    public ResponseEntity<RetornoTransacao> doConsultaTransacao(RestTemplate restTemplate, String urlConsultaTransacao, HttpEntity entity) {
        return restTemplate.exchange(
                        urlConsultaTransacao,
                        HttpMethod.GET,
                        entity,
                        RetornoTransacao.class
                        //        param
                );
    }

    //TODO - COMO AINDA NAO TEMOS STATUS DE PAGAMENTO DE ORDER, SERA NECESSARIO IMPLEMENTAR ESTE METODO POSTERIORMENTE
    public Boolean updatePaymentStatus(RetornoTransacao retornoTransacao) {

        return true;
    }
}
