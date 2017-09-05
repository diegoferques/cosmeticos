package com.cosmeticos.controller;

import com.cosmeticos.commons.CampainhaSuperpeyResponseBody;
import com.cosmeticos.model.*;
import com.cosmeticos.payment.superpay.client.rest.model.*;
import com.cosmeticos.repository.AddressRepository;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.PaymentRepository;
import com.cosmeticos.service.TypedCcPaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by matto on 08/08/2017.
 */
@Slf4j
@Controller
public class PaymentController {

    @Value("${superpay.url.transacao}")
    private String urlTransacao;

    @Value("${superpay.estabelecimento}")
    private String estabelecimento;

    @Value("${superpay.login}")
    private String login;

    @Value("${superpay.senha}")
    private String senha;

    @Autowired
    private TypedCcPaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public enum Language {
        NONE, PORTUGUESE, ENGLISH, SPANISH
    }

    //NO MANUAL DA SUPERPAY NAO INFORMA O QUE VEM EM CAMPOLIVRE E EH OPCIONAL, COM ISSO, NAO FACO NADA COM ELES.
    //https://superpay.acelerato.com/base-de-conhecimento/#/artigos/128
    @RequestMapping(path = "/campainha/superpay/", method = RequestMethod.POST)
    public HttpEntity<CampainhaSuperpeyResponseBody> create(
            @RequestParam(name = "numeroTransacao") Long numeroTransacao,
            @RequestParam(name = "codigoEstabelecimento") Long codigoEstabelecimento,
            @RequestParam(required = false, name = "campoLivre1") String campoLivre1,
            @RequestParam(required = false, name = "campoLivre2") String campoLivre2,
            @RequestParam(required = false, name = "campoLivre3") String campoLivre3,
            @RequestParam(required = false, name = "campoLivre4") String campoLivre4,
            @RequestParam(required = false, name = "campoLivre5") String campoLivre5) {

        CampainhaSuperpeyResponseBody responseBody = new CampainhaSuperpeyResponseBody();
        Optional<Order> orderOptional = Optional.ofNullable(orderRepository.findOne(numeroTransacao));

        try {

            if(numeroTransacao == null) {

                responseBody.setDescription("O campo \"numeroTransacao\" é obrigatório.");
                log.error(responseBody.getDescription());

                return badRequest().body(responseBody);

            } else if(codigoEstabelecimento == null) {

                responseBody.setDescription("O campo \"codigoEstabelecimento\" é obrigatório.");
                log.error(responseBody.getDescription());

                return badRequest().body(responseBody);

            } else if(codigoEstabelecimento != Long.parseLong(estabelecimento)) {

                responseBody.setDescription("\"codigoEstabelecimento\" desconhecido.");
                log.error(responseBody.getDescription());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);

            } else if(!orderOptional.isPresent()) {

                responseBody.setDescription("Order não encontrado:" + numeroTransacao);
                log.error(responseBody.getDescription());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);

            } else {
                //AQUI VAMOS CHAMAR O METODO QUE VAI NO SUPERPAY VERIFICAR SE HOUVE UMA ATUALIZACAO NA TRANSACAO
                //ESTE METODO SERA RESPONSAVEL PELA ATUALZICAO DO STATUS DO PAGAMENTO, QUE AINDA DEVERA SER IMPLEMENTADO
                Optional<RetornoTransacao> retornoConsulta = paymentService.consultaTransacao(numeroTransacao);

                if(retornoConsulta.isPresent()) {

                    Boolean updateStatus = paymentService.updatePaymentStatus(retornoConsulta.get());

                    if(updateStatus) {
                        responseBody.setDescription("Campainha sinalizada e status do pagamento para Order com o ID ["+
                                numeroTransacao +"] foram atualizados com sucesso.");
                        log.error(responseBody.getDescription());
                        return ok(responseBody);

                    } else {
                        String errorCode = String.valueOf(System.nanoTime());
                        responseBody.setDescription("Erro ao atualizar o status do pagamento da transação de ID ["+ numeroTransacao +"]. Error Code: ["+ errorCode +"].");
                        log.error(responseBody.getDescription());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
                    }

                } else {
                    String errorCode = String.valueOf(System.nanoTime());
                    responseBody.setDescription("Transação com ID ["+ numeroTransacao +"] não encontrada na Superpay. Error Code: ["+ errorCode +"].");
                    log.error(responseBody.getDescription());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
                }

            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            responseBody.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }


}
