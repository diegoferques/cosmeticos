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
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    @RequestMapping(path = "/customers", method = RequestMethod.GET)
    @ResponseBody
    String getCustomers() {


        CustomerResponseBody b = new CustomerResponseBody();

        return "Exibição de todos os Customers cadastrados";
    }

    @RequestMapping(path = "/customers", method = RequestMethod.POST)
    public HttpEntity<CustomerResponseBody> create(@Valid @RequestBody CustomerRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));
            } else {
                Customer c = service.create(request);
                log.info("Customer adicionado com sucesso:  [{}]", c);
                return ok().build();
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            CustomerResponseBody b = new CustomerResponseBody();
            b.setDescription("Erro interno: " + errorCode);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
        }

    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<CustomerResponseBody> get(@PathVariable String idCustomer) {

        CustomerResponseBody responseBody = new CustomerResponseBody();

        try {

            if(idCustomer.isEmpty()) {
                String erro = "Erro na requisicao do cliente: idCustomer não informado";

                responseBody.setDescription(erro);

                log.error(erro);

                return badRequest().body(responseBody);

            } else {
                Customer customer = service.find(Long.valueOf(idCustomer));
                responseBody.setCustomer(customer);

                log.info("Customer exibido com sucesso:  [{}]", customer);
                return ok().body(responseBody);
            }

        } catch (Exception e) {

            String errorCode = String.valueOf(System.nanoTime());
            String erro = "Erro interno: " + errorCode;

            responseBody.setDescription(erro);

            log.error("Erro na exibição do Customer ID: {} - {}", errorCode, e.getMessage(), e );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.PUT)
    @ResponseBody
    String updateCustomer(@PathVariable String idCustomer) {

        try {
            return "Atualizando o Customer de ID " + idCustomer;

        } catch (Exception e) {
            log.error("Erro na Atualização do Customer ID: {} - {}", idCustomer, e.getMessage(), e );
            return "Erro";
        }
    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.DELETE)
    @ResponseBody
    String deleteCustomer(@PathVariable String idCustomer) {

        try {
            return "Customer de ID " + idCustomer + " Deletado com sucesso!";

        } catch (Exception e) {
            log.error("Erro ao Deletar o Customer ID: {} - {}", idCustomer, e.getMessage(), e );
            return "Erro";
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
