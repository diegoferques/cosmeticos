package com.cosmeticos.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class Payment implements Serializable {

	public enum Type{
		CC, CASH, BOLETO
	}
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private Type type;
	
	private Long value;
	
	private Integer parcelas;

	@JoinColumn(name = "id_order", referencedColumnName = "idOrder")
	@ManyToOne(optional = false)
	private Order order;
	
	private PriceRule priceRule;
	
	@Transient
	private CreditCard creditCard;
	
	
}
