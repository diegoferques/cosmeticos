
package com.cosmeticos.payment.client;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Transacao {

    public Integer numeroTransacao;
    public Integer valor;
    public Integer valorDesconto;
    public Integer parcelas;
    public String urlCampainha;
    public String urlResultado;
    public String ip;
    public Integer idioma;
    public String campoLivre1;
    public String campoLivre2;
    public String campoLivre3;
    public String campoLivre4;
    public String campoLivre5;
    public String dataVencimentoBoleto;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
