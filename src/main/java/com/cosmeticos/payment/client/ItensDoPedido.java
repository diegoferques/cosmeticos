
package com.cosmeticos.payment.client;

import java.util.HashMap;
import java.util.Map;

public class ItensDoPedido {

    public Integer codigoProduto;
    public String nomeProduto;
    public Integer codigoCategoria;
    public String nomeCategoria;
    public Integer quantidadeProduto;
    public Integer valorUnitarioProduto;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
