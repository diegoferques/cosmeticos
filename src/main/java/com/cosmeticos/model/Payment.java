package com.cosmeticos.model;

import java.io.Serializable;

import javax.persistence.*;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Entity
@Data
public class Payment implements Serializable {

	public enum Type{
		CC, CASH, BOLETO
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
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id")
	private PriceRule priceRule;
	
	@Transient
	private CreditCard creditCard;

	public Payment() {
	}

	public Payment(Type cash) {
	}

}
