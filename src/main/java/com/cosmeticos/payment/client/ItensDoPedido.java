
package com.cosmeticos.payment.client;

import lombok.Data;

@Data
public class ItensDoPedido {

    public Integer codigoProduto;
    public String nomeProduto;
    public Integer codigoCategoria;
    public String nomeCategoria;
    public Integer quantidadeProduto;
    public Integer valorUnitarioProduto;

}
