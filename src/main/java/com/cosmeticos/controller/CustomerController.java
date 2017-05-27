package com.cosmeticos.controller;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.model.Customer;
import com.cosmeticos.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
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
        return "Exibição de todos os Customers cadastrados";
    }

    @RequestMapping(path = "/customers", method = RequestMethod.POST)
    public HttpEntity<CustomerResponseBody> create(@Valid @RequestBody CustomerRequestBody request,
                                                   BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
            return badRequest().body(buildErrorResponse(bindingResult));
        } else {
            Customer c = service.create(request);
            log.info("Customer adicionado com sucesso:  [{}]", c);
            return ok().build();
        }

        /*
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");

            log.info("Customer inserido com sucesso");

            return "Criando novo Customer " + name.toString();

        } catch (Exception e) {
            log.error("Erro no insert: {} - {}", name, e.getMessage(), e );
            return "Erro";
        }
        */
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

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.GET)
    @ResponseBody
    String getCustomer(@PathVariable String idCustomer) {

        try {
            return "Exibição do Customer de ID " + idCustomer;

        } catch (Exception e) {
            log.error("Erro na exibição do Customer ID: {} - {}", idCustomer, e.getMessage(), e );
            return "Erro";
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



}
