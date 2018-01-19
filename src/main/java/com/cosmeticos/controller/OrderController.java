package com.cosmeticos.controller;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.commons.OrderResponseBody;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.commons.UserResponseBody;
import com.cosmeticos.model.Order;
import com.cosmeticos.service.OrderService;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.MessageFormat;
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
    private OrderService orderService;

    @JsonView(ResponseJsonView.OrderControllerCreate.class)
    @RequestMapping(path = "/orders", method = RequestMethod.POST)
    public HttpEntity<OrderResponseBody> create(@Valid @RequestBody OrderRequestBody request,
                                                BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {


                MDC.put("idCustomer", String.valueOf(request.getOrder().getIdCustomer().getIdCustomer()));
                MDC.put("customerUserStatus", String.valueOf(request.getOrder().getIdCustomer().getStatus()));

                orderService.validateCreate(request.getOrder());

                Order order = orderService.create(request);

                MDC.put("newStatus", String.valueOf(order.getStatus()));

                log.info("Order adicionado com sucesso: ");//[{idProfessional: "+order.getProfessionalCategory().getProfessional().getIdProfessional()+"}, " +
                //" {idCustomer: "+order.getIdCustomer().getIdCustomer()+"}, " +
                //"{status atual: "+order.getHttpStatus()+"}]");

                //return ok().build();
                return ok(new OrderResponseBody(order));

            }
        } catch (OrderService.ValidationException e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return badRequest().body(orderResponseBody);
        } catch (OrderValidationException e) {

            String auditError = String.valueOf(System.nanoTime());
            String msg = MessageFormat.format("Falha da validacao da requisicao! responseCode: {0}, message: {1}", auditError, e.getMessage());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setResponseCode(e.getCode());
            orderResponseBody.setDescription(msg);
            orderResponseBody.setUserFriendlyMessage(e.getMessage());

            MDC.put("auditError", auditError);
            MDC.put("responseCode", String.valueOf(e.getCode()));
            MDC.put("httpStatus", String.valueOf(e.getCode().getHttpStatus()));

            log.error("Erro no insert: {} ", msg, e);

            return ResponseEntity.status(e.getCode().getHttpStatus()).body(orderResponseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setDescription("Erro interno: " + errorCode);

            MDC.put("responseCode", errorCode);
            MDC.put("httpStatus", String.valueOf(e.getMessage()));

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


                if (Order.Status.CANCELLED.equals(request.getOrder().getStatus())) {
                    orderService.abort(request.getOrder());
                } else {
                    // Pedidos de cancelamento devem ser acatados, nao devemos por obstaculos no desejo do usuario de cancelar. Nao no backend.
                    orderService.validateUpdate(request.getOrder());
                }

                Order order = orderService.update(request);


                MDC.put("idCustomer", String.valueOf(order.getIdCustomer().getIdCustomer()));
                MDC.put("customerUserStatus", String.valueOf(order.getIdCustomer().getStatus()));
                MDC.put("idProfessional", String.valueOf(order.getProfessionalCategory().getProfessional().getIdProfessional()));
                MDC.put("professionalUserStatus", String.valueOf(order.getProfessionalCategory().getProfessional().getStatus()));
                MDC.put("idOrder", String.valueOf(order.getIdOrder()));
                MDC.put("newStatus", String.valueOf(order.getStatus()));

                log.info("Order atualizado com sucesso.");

                OrderResponseBody responseBody = new OrderResponseBody(order);
                return ok(responseBody);

            }
        } catch (IllegalStateException e) {

            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription(e.getMessage());

            MDC.put("responseCode", errorCode);
            MDC.put("httpStatus", String.valueOf(e.getMessage()));

            log.error("Erro na atualização do Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        } catch (OrderValidationException e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody orderResponseBody = new OrderResponseBody();
            orderResponseBody.setDescription(e.getMessage());

            MDC.put("responseCode", errorCode);
            MDC.put("httpStatus", String.valueOf(e.getCode().getHttpStatus()));

            log.error("Erro no update: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(e.getCode().getHttpStatus()).body(orderResponseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            MDC.put("responseCode", errorCode);
            MDC.put("httpStatus", String.valueOf(e.getMessage()));

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

        log.warn("Ação não permitida para delete Order: {}. - {}", idOrder, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    }

    @JsonView(ResponseJsonView.OrderControllerFindBy.class)
    @RequestMapping(path = "/orders", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findBy() {

        try {
            //List<Order> entitylist = orderService.findBy(bindableQueryObject);
            List<Order> entitylist = orderService.findByStatusNotCancelledOrClosed();

            OrderResponseBody responseBody = new OrderResponseBody();
            responseBody.setOrderList(entitylist);
            responseBody.setDescription(entitylist.size() + " orders successfully retrieved.");

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

    @JsonView(ResponseJsonView.OrderControllerFindBy.class)
    @RequestMapping(path = "/orders/customer/", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findActiveByCustomer(
            @RequestParam(name = "email", required = true) String email
    ) {

        try {
            List<Order> entitylist = orderService.findActiveByCustomerEmail(email);

            if (entitylist.isEmpty()) {
                log.error("Nenhuma order encontrada para o customer.user.email={}", email);
                OrderResponseBody responseBody = new OrderResponseBody();
                responseBody.setDescription("No Orders could be found!");
                return status(HttpStatus.NOT_FOUND).body(responseBody);
            } else {
                OrderResponseBody responseBody = new OrderResponseBody();
                responseBody.setOrderList(entitylist);
                responseBody.setDescription(entitylist.size() + " retrieved.");

                log.info("{} Orders successfully retrieved.", entitylist.size());

                return ok().body(responseBody);
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            OrderResponseBody response = new OrderResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Order: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @JsonView(ResponseJsonView.OrderControllerFindBy.class)
    @RequestMapping(path = "/orders/professional/", method = RequestMethod.GET)
    public HttpEntity<OrderResponseBody> findActiveByProfessional(
            @RequestParam(name = "email", required = true) String email
    ) {

        try {
            List<Order> entitylist = orderService.findActiveByProfessionalEmail(email);

            if (entitylist.isEmpty()) {
                log.error("Nenhuma order encontrada para o profissional.user.email={}", email);
                OrderResponseBody responseBody = new OrderResponseBody();
                responseBody.setDescription("No Orders could be found!");
                return status(HttpStatus.NOT_FOUND).body(responseBody);
            } else {
                OrderResponseBody responseBody = new OrderResponseBody();
                responseBody.setOrderList(entitylist);
                responseBody.setDescription(entitylist.size() + " retrieved.");

                log.info("{} Orders successfully retrieved.", entitylist.size());

                return ok().body(responseBody);
            }
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
