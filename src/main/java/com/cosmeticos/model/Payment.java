package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.ResponseJsonView;
import com.cosmeticos.validation.OrderValidationException;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import javax.validation.Constraint;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

@Entity
@Data
public class Payment implements Serializable {

	/**
	 * Superpay só permite ate 8 caracteres: http://wiki.superpay.com.br/wikiSuperPay/index.php/Capturar_transação_SOAP
	 */
	@Size(max = 8)
	private String externalTransactionId;

	public enum Type{
		CC, CASH, BOLETO
	}

	public enum Status{
		PAGO_E_CAPTURADO(true, 1, HttpStatus.OK, ResponseCode.SUCCESS),
		PAGO_E_NAO_CAPTURADO(true, 2, HttpStatus.ACCEPTED, ResponseCode.SUCCESS),
		NAO_PAGO(false, 3, HttpStatus.FORBIDDEN, ResponseCode.FORBIDEN_PAYMENT),
		TRANSACAO_EM_ANDAMENTO(false, 5, HttpStatus.CONFLICT, ResponseCode.GATEWAY_DUPLICATE_PAYMENT),
		AGUARDANDO_PAGAMENTO(false, 8, HttpStatus.BAD_REQUEST, ResponseCode.UNFINISHED_PAYMENT),
		FALHA_NA_OPERADORA(false, 9, HttpStatus.BAD_GATEWAY, ResponseCode.GATEWAY_FAILURE),
		CANCELADA(false, 13, HttpStatus.GONE, ResponseCode.CANCELED_PAYMENT),
		ESTORNADA(false, 14, HttpStatus.GONE, ResponseCode.REFUNDED_PAYMENT),
		EM_ANALISE_DE_FRAUDE(true, 15, HttpStatus.ACCEPTED, ResponseCode.SUCCESS),
		RECUSADO_PELO_ANTI_FRAUDE(false, 17, HttpStatus.UNAUTHORIZED, ResponseCode.INVALID_PAYMENT_CONFIGURATION),
		FALHA_NA_ANTIFRAUDE(false, 18, HttpStatus.BAD_GATEWAY, ResponseCode.GATEWAY_FAILURE),
		BOLETO_PAGO_A_MENOR(false, 21, HttpStatus.NOT_IMPLEMENTED, ResponseCode.INTERNAL_ERROR),
		BOLETO_PAGO_A_MAIOR(false, 22, HttpStatus.NOT_IMPLEMENTED, ResponseCode.INTERNAL_ERROR),
		ESTORNO_PARCIAL(true, 23, HttpStatus.OK, ResponseCode.SUCCESS),
		ESTORNO_NAO_AUTORIZADO(false, 24, HttpStatus.UNAUTHORIZED, ResponseCode.INVALID_OPERATION),
		FALHA_ESTORNO(false, 25, HttpStatus.BAD_GATEWAY, ResponseCode.GATEWAY_FAILURE),
		TRANSACAO_EM_CURSO(false, 30, HttpStatus.CONFLICT, ResponseCode.GATEWAY_DUPLICATE_PAYMENT),

		/**
		 * Este caso eh um pouco complicado. Nao esta claro se pode ser um erro ou sucesso.
		 * Acho q podemo fazer vista grossa pra esse status e responder OK pro usuarios.
		 */
		TRANSACAO_JA_PAGA(true, 31, HttpStatus.OK, ResponseCode.SUCCESS),// vai quebrar testes que assertam conflict no status 31.
		AGUARDANDO_CANCELAMETO(true, 40, HttpStatus.ACCEPTED, ResponseCode.SUCCESS);

		private final Integer superpayStatus;

		/**
		 * @deprecated ResponseCode ja possui http status. Devemos usar o http status dentro do ResponseCode.
		 */
		@Deprecated
		private final HttpStatus httpStatus;
		private final ResponseCode responseCode;
		private boolean success;

		/**
		 *
		 * @param success Informa se consideramos o status retornado do superpay como sucesso ou nao. Simplifica nossos IFs.
		 * @param superpayStatus Codigo da superpay. Detalhes sobre este codigo na documentacao do superpay.
		 * @param status Http Status que determinamos que serao retornados ao App de acordo com cada status do superpay
		 * @param responseCode Codigo de resposta da nossa aplicacao. Consulte a documentacao dessa classe.
		 */
		private Status(Boolean success, Integer superpayStatus, HttpStatus status, ResponseCode responseCode) {
			this.httpStatus = status;
			this.superpayStatus = superpayStatus;
			this.success = success;
			this.responseCode = responseCode;
		}
		public HttpStatus getHttpStatus() {
			return httpStatus;
		}
		public Integer getSuperpayStatusTransacao() { return superpayStatus; }
		public boolean isSuccess() {
			return success;
		}
		public ResponseCode getResponseCode() {
			return responseCode;
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
	// O certo eh ser optional = false mas ta dando muito problema @ManyToOne(cascade = CascadeType.ALL, optional = false)
	@ManyToOne(optional = false)
	@JoinColumn(name = "price_rule_id", referencedColumnName = "id")
	private PriceRule priceRule;

	/**
	 * TODO: o cartao de credito que vem com Payment no request de abertura de order eh diferente do que gravamos no
	 * banco. No banco fica so o token e o que vem no request vem com dados completos.
	 * Precisamos de um outro objeto para representar ESTE cartao de credito. ATUALIZADO: talvez nao precisemos, pois
	 * o que vai ao banco fica associado ao user e nao ao payment.
	 */
	@Transient
	private CreditCard creditCard;

	public Payment() {
	}

	public Payment(Type type) {
		this.type = type;
	}

	public String getExternalTransactionId() {
		return externalTransactionId == null ? String.valueOf(id) : externalTransactionId;
	}

	public void setExternalTransactionId(String externalTransactionId) {
		// Superpay só permite ate 8 caracteres: http://wiki.superpay.com.br/wikiSuperPay/index.php/Capturar_transação_SOAP
		if(String.valueOf(externalTransactionId).length() > 8)
		{
			throw new IllegalStateException(
					"externalTransactionId com quantidade invalida de digitos: " + externalTransactionId);

		}
		else{
			this.externalTransactionId = externalTransactionId;
		}
	}
}
