package com.cosmeticos.payment.superpay.client.rest.model;

import lombok.Data;

@Data
public class Usuario {
    
    private String login;
    private String senha;

    Usuario() { }
    
    public Usuario(String login, String senha) {
	this.login = login;
	this.senha = senha;
    }

}
