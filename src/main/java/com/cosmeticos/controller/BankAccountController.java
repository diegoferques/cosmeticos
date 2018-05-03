package com.cosmeticos.controller;

import com.cosmeticos.commons.Balance;
import com.cosmeticos.model.BankAccount;
import com.cosmeticos.service.BankAccountService;
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
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    @RequestMapping(path = "/bankAccount/{id}", method = RequestMethod.PUT)
    public HttpEntity<Balance> updateBankAccount(@PathVariable("id") Long bankAccountId,
                                                 @Valid @RequestBody BankAccount bankAccount,
                                                 BindingResult bindingResult) {

        try {

            if (bindingResult.hasErrors()) {
                log.error("Erros na requisicao do cliente: {}", bindingResult.toString());
                return badRequest().header(
                        "badRequestDetails",
                        buildErrorResponse(bindingResult)
                )
                .build();
            }
            else{

                Optional<BankAccount> optionalBA = bankAccountService.findById(bankAccountId);

                if (optionalBA.isPresent()) {

                    bankAccount.setId(optionalBA.get().getId());
                    bankAccountService.update(bankAccount);

                    // Nao respondemos com body pq a conta do usuario nao é algo que queremos ficar trafegando pela rede por enquanto..
                    return ok().build();
                } else {
                    log.error("Nenhuma conta encontrada com o id: {}",  bankAccountId);
                    return notFound().build();
                }
            }

        } catch (Exception e) {
            log.error("Falha atualizando conta bancaria bankAccountId: "+ bankAccountId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String buildErrorResponse(BindingResult bindingResult) {
        List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(fieldError -> bindingResult.getFieldError(fieldError.getField()).getDefaultMessage())
                .collect(Collectors.toList());

        return errors.toString();
    }
}
