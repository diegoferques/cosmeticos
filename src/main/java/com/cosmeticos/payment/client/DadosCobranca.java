
package com.cosmeticos.payment.client;

import lombok.Data;

import java.util.List;

@Data
public class DadosCobranca {

    public Integer codigoCliente;
    public Integer tipoCliente;
    public String nome;
    public String email;
    public String dataNascimento;
    public String sexo;
    public String documento;
    public String documento2;
    public Endereco endereco;
    public List<Telefone> telefone = null;

}
