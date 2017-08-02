
package com.cosmeticos.payment.client;

import java.util.HashMap;
import java.util.Map;

public class DadosCartao {

    public String nomePortador;
    public String numeroCartao;
    public String codigoSeguranca;
    public String dataValidade;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
