
package com.cosmeticos.payment.client;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RequisicaoTransacao {

    public Integer codigoEstabelecimento;
    public Integer codigoFormaPagamento;
    public Transacao transacao;
    public DadosCartao dadosCartao;
    public List<ItensDoPedido> itensDoPedido = null;
    public DadosCobranca dadosCobranca;
    public DadosEntrega dadosEntrega;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
