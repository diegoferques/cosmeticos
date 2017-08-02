
package com.cosmeticos.payment.client;

import java.util.HashMap;
import java.util.Map;

public class Endereco_ {

    public String logradouro;
    public String numero;
    public String complemento;
    public String cep;
    public String bairro;
    public String cidade;
    public String estado;
    public String pais;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
