
package com.cosmeticos.payment.client;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RetornoTransacao {

    public Integer numeroTransacao;
    public String codigoEstabelecimento;
    public Integer codigoFormaPagamento;
    public Integer valor;
    public Integer valorDesconto;
    public Integer parcelas;
    public Integer statusTransacao;
    public String autorizacao;
    public String codigoTransacaoOperadora;
    public String dataTransacaoOperadora;
    public String dataAprovacaoOperadora;
    public String numeroComprovanteVenda;
    public String nsu;
    public String mensagemVenda;
    public String urlPagamento;
    public List<String> cartoesUtilizados = null;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
