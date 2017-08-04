
package com.cosmeticos.payment.client;

import lombok.Data;

import java.util.List;

@Data
public class DadosEntrega {

    public String nome;
    public String email;
    public String dataNascimento;
    public String sexo;
    public String documento;
    public String documento2;
    public Endereco_ endereco;
    public List<Telefone_> telefone = null;

}
