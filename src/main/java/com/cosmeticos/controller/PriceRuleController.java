package com.cosmeticos.controller;

import com.cosmeticos.commons.PriceRuleResponseBody;
import com.cosmeticos.model.PriceRule;
import com.cosmeticos.service.PriceRuleService;
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
public class PriceRuleController {

    @Autowired
    private PriceRuleService priceRuleService;

    @RequestMapping(path = "/priceRules", method = RequestMethod.POST)
    public HttpEntity<PriceRuleResponseBody> create(@Valid @RequestBody PriceRule request,
                                                    BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().body(buildErrorResponse(bindingResult));

            } else {

                PriceRule priceRule = priceRuleService.create(request);
                log.info("PriceRule adicionado com sucesso:  [{}]", priceRule);
                return ok(new PriceRuleResponseBody(priceRule));
            }
        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            PriceRuleResponseBody b = new PriceRuleResponseBody();
            b.setDescription("Erro interno: " + errorCode);

            log.error("Erro no insert: {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(b);
        }
    }

    @RequestMapping(path = "/priceRules/{id}", method = RequestMethod.DELETE)
    public HttpEntity<Void> delete(
            @PathVariable("id") Long id
    ) {
        try {

            priceRuleService.delete(id);

            log.info("PriceRule {} deletada com sucesso.", id);

            return ok().build();

        } catch (Exception e) {
            log.error("Failed to retrieve ProfessionalServices: {}", e.getMessage(), e);

            return ResponseEntity.status(500).build();
        }

    }


    private PriceRuleResponseBody buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        PriceRuleResponseBody responseBody = new PriceRuleResponseBody();
        responseBody.setDescription(errors.toString());
        return responseBody;
    }

}