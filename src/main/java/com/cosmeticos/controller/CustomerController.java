package com.cosmeticos.controller;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.model.Customer;
import com.cosmeticos.service.CustomerService;
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

@Slf4j
@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    @RequestMapping(path = "/customers", method = RequestMethod.POST)
    public HttpEntity<CustomerResponseBody> create(@Valid @RequestBody CustomerRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Customer customer = service.create(request);
                log.info("Customer adicionado com sucesso:  [{}]", customer);
                //return ok().build();
                return ok(new CustomerResponseBody(customer));
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            CustomerResponseBody b = new CustomerResponseBody();
            b.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
        }
    }

    @RequestMapping(path = "/customers", method = RequestMethod.PUT)
    public HttpEntity<CustomerResponseBody> update(@Valid @RequestBody CustomerRequestBody request, BindingResult bindingResult) {

        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            }
            else
            {
                Customer customer = service.update(request);
                log.info("Customer atualizado com sucesso:  [{}]", customer);
                return ok(new CustomerResponseBody(customer));
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            CustomerResponseBody response = new CustomerResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na atualização do Customer: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.GET)
    public HttpEntity<CustomerResponseBody> findById(@PathVariable String idCustomer) {

        try {

            Optional<Customer> customer = service.find(Long.valueOf(idCustomer));

            if (customer.isPresent()) {
                log.info("Busca de Customer com exito: [{}]", customer.get());
                CustomerResponseBody response = new CustomerResponseBody(customer.get());

                //return ok().body(response);
                return ok(response);
            } else {
                log.error("Nenhum registro encontrado para o id: {}", idCustomer);
                return notFound().build();
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            CustomerResponseBody response = new CustomerResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição do Customer: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.DELETE)
    public HttpEntity<CustomerResponseBody> delete(@PathVariable String idCustomer) {


        String errorCode = String.valueOf(System.nanoTime());

        CustomerResponseBody response = new CustomerResponseBody();
        response.setDescription("Ação não permitida: Atualize o status do Customer para desativado: " + errorCode);

        log.warn("Ação não permitida para deletar o Customer: {}. Atualize o status do Customer para desativado. - {}", idCustomer, errorCode);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

    }

    @RequestMapping(path = "/customers", method = RequestMethod.GET)
    public HttpEntity<CustomerResponseBody> findLastest10() {

        try {
            List<Customer> entitylist = service.find10Lastest();

            CustomerResponseBody responseBody = new CustomerResponseBody();
            responseBody.setCustomerList(entitylist);
            responseBody.setDescription("TOP 10 successfully retrieved.");

            log.info("{} schedules successfully retrieved.", entitylist.size());

            return ok().body(responseBody);

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            CustomerResponseBody response = new CustomerResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na exibição da Lista de Customer: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private CustomerResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        CustomerResponseBody responseBody = new CustomerResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }


}
