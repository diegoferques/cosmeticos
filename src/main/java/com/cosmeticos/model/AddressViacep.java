package com.cosmeticos.model;

import lombok.Data;

/**
 * Created by matto on 09/02/2018.
 */
@Data
public class AddressViacep {

    public String cep;
    public String logradouro;
    public String complemento;
    public String bairro;
    public String localidade;
    public String uf;
    public String unidade;
    public String ibge;
    public String gia;
}
