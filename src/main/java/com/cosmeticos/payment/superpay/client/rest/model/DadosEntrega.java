package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

import java.util.List;

@Data
public class DadosEntrega {
    private String nome;
    private String email;
    private String dataNascimento;
    private String sexo;
    private String documento;
    private String documento2;
    private Endereco endereco;
    private List<Telefone> telefone;

}
