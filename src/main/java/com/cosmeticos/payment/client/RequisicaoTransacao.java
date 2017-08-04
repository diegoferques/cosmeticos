
package com.cosmeticos.payment.client;

import lombok.Data;

import java.util.List;

@Data
public class RequisicaoTransacao {

    public Integer codigoEstabelecimento;
    public Integer codigoFormaPagamento;
    public Transacao transacao;
    public DadosCartao dadosCartao;
    public List<ItensDoPedido> itensDoPedido = null;
    public DadosCobranca dadosCobranca;
    public DadosEntrega dadosEntrega;

}
