
package com.cosmeticos.payment.client;

import lombok.Data;

@Data
public class Endereco {

    public String logradouro;
    public String numero;
    public String complemento;
    public String cep;
    public String bairro;
    public String cidade;
    public String estado;
    public String pais;

}
