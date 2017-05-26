package com.cosmeticos.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class CustomerController {

    @RequestMapping(path = "/customers", method = RequestMethod.GET)
    @ResponseBody
    String getCustomers() {
        return "Exibição de todos os Customers cadastrados";
    }

    @RequestMapping(path = "/customers", method = RequestMethod.POST)
    @ResponseBody
    public String setCustomer(HttpServletResponse response,

                              @RequestParam(required = false) String name) {

        try {
            response.setHeader("Access-Control-Allow-Origin", "*");

            log.info("Customer inserido com sucesso");

            return "Criando novo Customer " + name.toString();

        } catch (Exception e) {
            log.error("Erro no insert: {} - {}", name, e.getMessage(), e );
            return "Erro";
        }

    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.GET)
    @ResponseBody
    String getCustomer(@PathVariable String idCustomer) {

        return "Exibição do Customer de ID " + idCustomer;
    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.PUT)
    @ResponseBody
    String updateCustomer(@PathVariable String idCustomer) {

        return "Atualizando o Customer de ID " + idCustomer;
    }

    @RequestMapping(path = "/customers/{idCustomer}", method = RequestMethod.DELETE)
    @ResponseBody
    String deleteCustomer(@PathVariable String idCustomer) {

        return "Customer de ID " + idCustomer + " Deletado com sucesso!";
    }



}
