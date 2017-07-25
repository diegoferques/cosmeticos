package com.cosmeticos.controller;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.model.Order;
import com.cosmeticos.penalty.PenaltyService;
import com.cosmeticos.service.OrderService;
import com.fasterxml.jackson.annotation.JsonView;
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

import static org.springframework.http.ResponseEntity.*;

/**
 * Created by matto on 17/06/2017.
 */
@Slf4j
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    PenaltyService penaltyService;

    @JsonView(ResponseJsonView.OrderControllerCreate.class)
    @RequestMapping(path = "/orders", method = RequestMethod.POST)
    public HttpEntity<OrderResponseBody> create(@Valid @RequestBody OrderRequestBody request,
                                                BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Order order = orderService.create(request);
                log.info("Order adicionado com sucesso:  [{}]", order);
                //return ok().build();
                return ok(new OrderResponseBody(order));
            }
        } catch (OrderService.ValidationException e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return badRequest().body(orderResponseBody);
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(orderResponseBody);
        }
    }

    @JsonView(ResponseJsonView.OrderControllerUpdate.class)
    @RequestMapping(path = "/orders", method = RequestMethod.PUT)
    public HttpEntity<OrderResponseBody> update(@Valid @RequestBody OrderRequestBody request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else {
                if (request.getOrder().getStatus() == Order.Status.CANCELLED) {
                    orderService.abort(request.getOrder());
                }
                Order order = orderService.update(request);
                order.setIdCustomer(null);//TODO: criar card de bug pra resolver relacionamento de Garry que eh Joao.
                order.setProfessionalServices(null);//TODO: criar card de bug pra resolver relacionamento de Garry que eh Joao.

                order.setProfessionalServices(null);
                order.setIdCustomer(null);

                OrderResponseBody responseBody = new OrderResponseBody(order);
                log.info("Order atualizado com sucesso:  [{}] responseJson[{}]",
                        order,
                        new ObjectMapper().writeValueAsString(responseBody));
                return ok(responseBody);

            }
        } catch (IllegalStateException e) {

            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription(e.getMessage());

            log.error("Erro na atualização do Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @JsonView(ResponseJsonView.OrderControllerUpdate.class)
    @RequestMapping(path = "/orders/{idOrder}", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findById(@PathVariable String idOrder) {

        try {

            Optional<Order> order = orderService.find(Long.valueOf(idOrder));

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

    @JsonView(ResponseJsonView.OrderControllerFindBy.class)
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findBy(@ModelAttribute Order bindableQueryObject) {

        try {
            //List<Order> entitylist = orderService.findBy(bindableQueryObject);
            List<Order> entitylist = orderService.findByStatusNotCancelledOrClosed();

            OrderResponseBody responseBody = new OrderResponseBody();
            responseBody.setOrderList(entitylist);
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
