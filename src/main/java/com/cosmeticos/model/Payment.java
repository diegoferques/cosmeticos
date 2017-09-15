package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

@Entity
@Data
public class Payment implements Serializable {

	public enum Type{
		CC, CASH, BOLETO
	}

	public enum Status{
		PAGO_E_CAPTURADO(true, 1, HttpStatus.OK),
		PAGO_E_NAO_CAPTURADO(true, 2, HttpStatus.ACCEPTED),
		NAO_PAGO(false, 3, HttpStatus.FORBIDDEN),
		TRANSACAO_EM_ANDAMENTO(false, 5, HttpStatus.CONFLICT),
		AGUARDANDO_PAGAMENTO(false, 8, HttpStatus.BAD_REQUEST),
		FALHA_NA_OPERADORA(false, 9, HttpStatus.BAD_GATEWAY),
		CANCELADA(false, 13, HttpStatus.GONE),
		ESTORNADA(false, 14, HttpStatus.GONE),
		EM_ANALISE_DE_FRAUDE(true, 15, HttpStatus.ACCEPTED),
		RECUSADO_PELO_ANTI_FRAUDE(false, 17, HttpStatus.UNAUTHORIZED),
		FALHA_NA_ANTIFRAUDE(false, 18, HttpStatus.BAD_GATEWAY),
		BOLETO_PAGO_A_MENOR(false, 21, HttpStatus.NOT_IMPLEMENTED),
		BOLETO_PAGO_A_MAIOR(false, 22, HttpStatus.NOT_IMPLEMENTED),
		ESTORNO_PARCIAL(true, 23, HttpStatus.OK),
		ESTORNO_NAO_AUTORIZADO(false, 24, HttpStatus.UNAUTHORIZED),
		FALHA_ESTORNO(false, 25, HttpStatus.BAD_GATEWAY),
		TRANSACAO_EM_CURSO(false, 30, HttpStatus.CONFLICT),

		/**
		 * Este caso eh um pouco complicado. Nao esta claro se pode ser um erro ou sucesso.
		 * Acho q podemo fazer vista grossa pra esse status e responder OK pro usuarios.
		 */
		TRANSACAO_JA_PAGA(true, 31, HttpStatus.OK),// vai quebrar testes que assertam conflict no status 31.
		AGUARDANDO_CANCELAMETO(true, 40, HttpStatus.ACCEPTED);

		private final Integer superpayStatus;		private final HttpStatus httpStatus;		private boolean success;

		private Status(Boolean success, Integer superpayStatus, HttpStatus status) {
			this.httpStatus = status;
			this.superpayStatus = superpayStatus;
			this.success = success;
		}
		public HttpStatus getHttpStatus() {
			return httpStatus;
		}
		public Integer getSuperpayStatusTransacao() { return superpayStatus; }
		public boolean isSuccess() {
			return success;
		}

		public static Status fromSuperpayStatus(Integer superpayStatusStransacao) {
			Optional<Status> paymentStatus = Arrays.asList(Payment.Status.values()).stream()
					.filter( status -> status.getSuperpayStatusTransacao().equals(superpayStatusStransacao))
					.findFirst();

			if(paymentStatus.isPresent())
			{
				return paymentStatus.get();
			}
			else
			{
				throw new IllegalArgumentException("Status do superpay nao mapeado: " + superpayStatusStransacao);
			}
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
