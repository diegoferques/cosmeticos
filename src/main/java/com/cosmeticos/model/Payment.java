package com.cosmeticos.model;

import java.io.Serializable;

import javax.persistence.*;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Payment implements Serializable {

	public enum Type{
		CC, CASH, BOLETO
	}

	public enum Status {
		PAGO_E_CAPTURADO(HttpStatus.OK), PAGO_E_NAO_CAPTURADO(HttpStatus.ACCEPTED), NAO_PAGO(HttpStatus.FORBIDDEN),
		TRANSACAO_EM_ANDAMENTO(HttpStatus.CONFLICT), AGUARDANDO_PAGAMENTO(HttpStatus.BAD_REQUEST),
		FALHA_NA_OPERADORA(HttpStatus.BAD_GATEWAY), CANCELADA(HttpStatus.GONE), ESTORNADA(HttpStatus.GONE),
		EM_ANALISE_DE_FRAUDE(HttpStatus.ACCEPTED), RECUSADO_PELO_ANTI_FRAUDE(HttpStatus.UNAUTHORIZED),
		FALHA_NA_ANTIFRAUDE(HttpStatus.BAD_GATEWAY), BOLETO_PAGO_A_MENOR(HttpStatus.NOT_IMPLEMENTED),
		BOLETO_PAGO_A_MAIOR(HttpStatus.NOT_IMPLEMENTED), ESTORNO_PARCIAL(HttpStatus.OK),
		ESTORNO_NAO_AUTORIZADO(HttpStatus.UNAUTHORIZED), TRANSACAO_EM_CURSO(HttpStatus.CONFLICT),
		TRANSACAO_JA_PAGA(HttpStatus.CONFLICT), AGUARDANDO_CANCELAMETO(HttpStatus.ACCEPTED);

		private HttpStatus httpStatus;

		private Status(HttpStatus status) {
			this.httpStatus = status;
		}

		public HttpStatus getHttpStatus() {
			return httpStatus;
		}
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
