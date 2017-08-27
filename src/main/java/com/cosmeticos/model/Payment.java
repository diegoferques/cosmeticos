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
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private Type type;
	
	private Long value;
	
	private Integer parcelas;
	
	@Transient
	private CreditCard creditCard;
}
