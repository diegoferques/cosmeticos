
package com.cosmeticos.payment.client;

import java.util.HashMap;
import java.util.Map;

public class Telefone {

    public String tipoTelefone;
    public String ddi;
    public String ddd;
    public String telefone;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
