
package com.cosmeticos.payment.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
