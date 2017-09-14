package com.cosmeticos.commons;

/**
 * Created by Vinicius on 13/09/2017.
 */
public enum SuperpayFormaPagamento {

    VISA(170), MASTERCARD(171);

    public int formaPagamento;

    private SuperpayFormaPagamento(int i) {
        this.formaPagamento = i;
    }

    public int fromCardType(){
        return formaPagamento;
    }
}
