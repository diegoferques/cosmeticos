package com.cosmeticos.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Payment implements Serializable {

	public enum Type{
		CC, CASH, BOLETO
	}

	public enum Status{
		PAGO_E_CAPTURADO, PAGO_E_NAO_CAPTURADO, NAO_PAGO, TRANSACAO_EM_ANDAMENTO, AGUARDANDO_PAGAMENTO,
		FALHA_NA_OPERADORA, CANCELADA, ESTORNADA, EM_ANALISE_DE_FRAUDE, RECUSADO_PELO_ANTI_FRAUDE, FALHA_NA_ANTIFRAUDE,
		BOLETO_PAGO_A_MENOR, BOLETO_PAGO_A_MAIOR, ESTORNO_PARCIAL, ESTORNO_NAO_AUTORIZADO, TRANSACAO_EM_CURSO,
		TRANSACAO_JA_PAGA, AGUARDANDO_CANCELAMETO
	}

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private Type type;

	@Enumerated(EnumType.STRING)
	private Status status;
	
	private Long value;
	
	private Integer parcelas;
	
	@Transient
	private CreditCard creditCard;
}
