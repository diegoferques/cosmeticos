
package com.cosmeticos.payment.client;

import lombok.Data;

@Data
public class DadosCartao {

    public String nomePortador;
    public String numeroCartao;
    public String codigoSeguranca;
    public String dataValidade;

}
