package com.cosmeticos.controller;

import com.cosmeticos.commons.Balance;
import com.cosmeticos.commons.ProfessionalResponseBody;
import com.cosmeticos.model.BalanceItem;
import com.cosmeticos.service.BalanceItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
public class BalanceItemController {

    @Autowired
    private BalanceItemService balanceItemService;

    @RequestMapping(path = "/balanceItens", method = RequestMethod.GET)
    public HttpEntity<Balance> findBalances(@ModelAttribute BalanceItem balanceItem) {

        try {

            if (StringUtils.isEmpty(balanceItem.getEmail())) {
                log.error("Email deve ser informado");
                Balance balance = new Balance();
                balance.setMessage("E-mail deve ser informado para listagem de saldo.");
                return badRequest().body(balance);
            } else {
                List<BalanceItem> balanceItems = balanceItemService.listBy(balanceItem);

                if (!balanceItems.isEmpty()) {

                    if (log.isDebugEnabled()) {
                        log.debug(balanceItems.toString());
                    }

                    log.info("Busca de saldo com exito: [{}]", balanceItem.getEmail());

                    Balance balance = new Balance();
                    balance.setBalanceItemList(balanceItems);
                    balance.setAvailableBalance(balanceItemService.sum(balanceItems));

                    return ok(balance);
                } else {
                    log.error("Nenhum registro de saldo encontrado para: {}", balanceItem.getEmail());
                    return notFound().build();
                }
            }

        } catch (Exception e) {
            String errorCode = String.valueOf(System.nanoTime());

            ProfessionalResponseBody response = new ProfessionalResponseBody();
            response.setDescription("Erro interno: " + errorCode);

            log.error("Erro na listagem de saldo do Professional: " + balanceItem.getEmail() + " {} - {}", errorCode, e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Balance());
        }
    }

    @Profile("default")
    @RequestMapping(path = "/balanceItens/all", method = RequestMethod.GET)
    public HttpEntity<Balance> findAll() {

        List<BalanceItem> balanceItems = balanceItemService.listAll();

        Balance balance = new Balance();
        balance.setBalanceItemList(balanceItems);

        return ok(balance);
    }

}
