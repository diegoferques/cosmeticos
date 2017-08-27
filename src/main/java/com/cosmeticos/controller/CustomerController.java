package com.cosmeticos.controller;

import com.cosmeticos.commons.CustomerRequestBody;
import com.cosmeticos.commons.CustomerResponseBody;
import com.cosmeticos.model.Customer;
import com.cosmeticos.service.CustomerService;
import com.cosmeticos.service.UserService;
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

@Slf4j
@RestController
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/customers", method = RequestMethod.POST)
    public HttpEntity<CustomerResponseBody> create(@Valid @RequestBody CustomerRequestBody request,
                                                   BindingResult bindingResult) {
        try {
            if(bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else {

                String userEmail = "";
                if(request.getCustomer().getUser() != null) {
                    userEmail = request.getCustomer().getUser().getEmail();
                }

                if(userService.verifyEmailExists(userEmail)) {

                    CustomerResponseBody responseBody = new CustomerResponseBody();
                    responseBody.setDescription("E-mail já existente.");
                    log.error("Nao e permitido atualizar usuario para um email pre-existentes.");

                    return badRequest().body(responseBody);

                } else {
                    Customer customer = service.create(request);
                    log.info("Customer adicionado com sucesso:  [{}]", customer);
                    //return ok().build();
                    return ok(new CustomerResponseBody(customer));
                }
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
    public HttpEntity<CustomerResponseBody> update(@RequestBody CustomerRequestBody request,
                                                   BindingResult bindingResult) {

        try {
			if (bindingResult.hasErrors()) {
				log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
				return badRequest().body(buildErrorResponse(bindingResult));

			} else {

				Optional<Customer> optional = service.find(request.getCustomer().getIdCustomer());

				if (optional.isPresent()) {

					Customer persistentCustomer = optional.get();

					String emailInDatabase = persistentCustomer.getUser().getEmail();
					String emailFromRequest = request.getCustomer().getUser().getEmail();

					if (emailInDatabase.equals(emailFromRequest)) {

						optional = Optional.ofNullable(service.update(request));

						Customer customer = optional.get();

						CustomerResponseBody responseBody = new CustomerResponseBody(customer);
						log.info("Customer atualizado com sucesso: id[{}]", customer.getIdCustomer());
						return ok(responseBody);
					} else {
						CustomerResponseBody responseBody = new CustomerResponseBody();
						responseBody.setDescription("E-mail já existente.");
						log.error("Nao e permitido atualizar cliente para um email já existente.");

						return badRequest().body(responseBody);
					}
				} else {
					log.info("Customer inexistente:  [{}]", request.getCustomer());
					return ResponseEntity.notFound().build();
				}
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
    public HttpEntity<CustomerResponseBody> findById(@PathVariable Long idCustomer) {

        try {

            Optional<Customer> customer = service.find(idCustomer);

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

    @RequestMapping(path = "/customers/", method = RequestMethod.GET)
    public HttpEntity<CustomerResponseBody> findById(@ModelAttribute Customer customer) {

        try {

            List<Customer> customers = service.findBy(customer);

            if (!customers.isEmpty()) {

                log.info(customers.size() + " encontrados: [{}]", customer);
                CustomerResponseBody response = new CustomerResponseBody();
                response.setCustomerList(customers);

                //return ok().body(response);
                return ok(response);
            } else {
                log.error("Nenhum registro encontrado para customer: {}", customer);
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
    public HttpEntity<CustomerResponseBody> findAll() {

        try {
            List<Customer> entitylist = service.findAll();

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
