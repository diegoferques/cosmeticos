package com.cosmeticos.commons;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Vinicius on 13/09/2017.
 */
public enum SuperpayFormaPagamento {

    VISA(170), MASTERCARD(171);

    private Integer codigoFormaPagamento;

    private SuperpayFormaPagamento(Integer i) {
        this.codigoFormaPagamento = i;
    }

    public SuperpayFormaPagamento fromCardType(Integer superpayPagamento){
        Optional<SuperpayFormaPagamento> formaPagamentoOptional = Arrays.asList(SuperpayFormaPagamento.values())
                .stream()
                .filter(forma -> forma.getCodigoFormaPagamento() == superpayPagamento)
                .findFirst();

        if (formaPagamentoOptional.isPresent()) {
            return formaPagamentoOptional.get();
        } else {
            throw new IllegalArgumentException("Forma de pagamento indisponivel: " + superpayPagamento);
        }
    }

    public Integer getCodigoFormaPagamento() {
        return codigoFormaPagamento;
    }
}
