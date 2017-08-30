package com.cosmeticos.model;

import java.io.Serializable;

import javax.persistence.*;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
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

	@JsonView({
			ResponseJsonView.OrderControllerCreate.class,
			ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class
	})
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView({
			ResponseJsonView.OrderControllerCreate.class,
			ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class
	})
	@Enumerated(EnumType.STRING)
	private Type type;

	@Enumerated(EnumType.STRING)
	private Status status;

	private Long value;

	@JsonView({
			ResponseJsonView.OrderControllerCreate.class,
			ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class
	})
	private Integer parcelas;

	@JoinColumn(name = "id_order", referencedColumnName = "idOrder")
	@ManyToOne(optional = false)
	private Order order;

	@JsonView({
			ResponseJsonView.OrderControllerCreate.class,
			ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class
	})
	@OneToOne
	@JoinColumn(name = "id")
	private PriceRule priceRule;

	@Transient
	private CreditCard creditCard;

	public Payment() {
	}

	public Payment(Type cash) {
	}

}
