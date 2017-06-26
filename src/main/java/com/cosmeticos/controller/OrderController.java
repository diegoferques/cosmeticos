package com.cosmeticos.controller;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.model.Sale;
import com.cosmeticos.model.Sale;
import com.cosmeticos.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

/**
 * Created by matto on 17/06/2017.
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @RequestMapping(path = "/orders", method = RequestMethod.POST)
    public HttpEntity<OrderResponseBody> create(@Valid @RequestBody OrderRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Sale sale = orderService.create(request);
                log.info("Order adicionado com sucesso:  [{}]", sale);
                //return ok().build();
                return ok(new OrderResponseBody(sale));
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(orderResponseBody);
        }
    }

    @RequestMapping(path = "/orders", method = RequestMethod.PUT)
    public HttpEntity<OrderResponseBody> update(@Valid @RequestBody OrderRequestBody request, BindingResult bindingResult) {

        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else {
                Sale sale = orderService.update(request);

                OrderResponseBody responseBody = new OrderResponseBody(sale);
                log.info("Order atualizado com sucesso:  [{}] responseJson[{}]",
                        sale,
                        new ObjectMapper().writeValueAsString(responseBody));
                return ok(responseBody);
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(path = "/orders/{idOrder}", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findById(@PathVariable String idOrder) {

        try {

            Optional<Sale> order = orderService.find(Long.valueOf(idOrder));

            if (order.isPresent()) {
                log.info("Busca de Order com exito: [{}]", order.get());
                OrderResponseBody response = new OrderResponseBody(order.get());

                //return ok().body(response);
                return ok(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", idOrder);
                return notFound().build();
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição de Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(path = "/orders/{idOrder}", method = RequestMethod.DELETE)
    public HttpEntity<OrderResponseBody> delete(@PathVariable String idOrder) {

        String errorCode = String.valueOf(System.nanoTime());

        OrderResponseBody response = new OrderResponseBody();
        response.setDescription("Ação não permitida: Order não pode ser deletado: " + errorCode);

        log.warn("Ação não permitida para deletar Order: {}. - {}", idOrder, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    }

    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findLastest10() {

        try {
            List<Sale> entitylist = orderService.find10Lastest();

            OrderResponseBody responseBody = new OrderResponseBody();
            responseBody.setSaleList(entitylist);
            responseBody.setDescription("TOP 10 successfully retrieved.");

            log.info("{} Orders successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private OrderResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        OrderResponseBody responseBody = new OrderResponseBody();
        responseBody.setDescription(errors.toString());

        return responseBody;
    }


}
