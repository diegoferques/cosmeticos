/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import static com.cosmeticos.model.Order.Status.ACCEPTED;
import static com.cosmeticos.model.Order.Status.CANCELLED;
import static com.cosmeticos.model.Order.Status.CLOSED;
import static com.cosmeticos.model.Order.Status.INPROGRESS;
import static com.cosmeticos.model.Order.Status.OPEN;
import static com.cosmeticos.model.Order.Status.SCHEDULED;
import static com.cosmeticos.model.Order.Status.SEMI_CLOSED;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
@Table(name = "[ORDER]")
public class Order implements Serializable {

	public enum Status {
		OPEN, CANCELLED, EXECUTED, SEMI_CLOSED, FINISHED_BY_CUSTOMER_AUTO, CLOSED, SCHEDULED, INPROGRESS, ACCEPTED
	}

	private static final long serialVersionUID = 1L;

	@JsonView({ ResponseJsonView.OrderControllerCreate.class, ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class })
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idOrder;

	@JsonView({ ResponseJsonView.OrderControllerCreate.class, ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class })
	// @Basic(optional = false)
	// @Column(name = "date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@JsonView({ ResponseJsonView.OrderControllerCreate.class, ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class })
	@Basic(optional = false)
	@Column(name = "status")
	private Integer status;

	@JsonView({ ResponseJsonView.OrderControllerCreate.class, ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class })
	@JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId")
	@ManyToOne(cascade = CascadeType.ALL)
	private Schedule scheduleId;

	@JsonView({ ResponseJsonView.OrderControllerCreate.class, ResponseJsonView.OrderControllerFindBy.class })
	@JoinColumns({ @JoinColumn(name = "idProfessional", referencedColumnName = "idProfessional"),
			@JoinColumn(name = "idService", referencedColumnName = "idService") })
	@ManyToOne(optional = false)
	private ProfessionalServices professionalServices;

	@JoinColumn(name = "idLocation", referencedColumnName = "id")
	@ManyToOne(optional = true)
	private Location idLocation;

	@JsonView({ ResponseJsonView.OrderControllerCreate.class, ResponseJsonView.OrderControllerFindBy.class })
	@JoinColumn(name = "idCustomer", referencedColumnName = "idCustomer")
	@ManyToOne(optional = false)
	private Customer idCustomer;

	/**
	 * "(Status atual, Status permitidos[])"
	 */
	private Map<Order.Status, Order.Status[]> statusChangeMatrix;

	public Order() {
		statusChangeMatrix = new HashMap<>();
		statusChangeMatrix.put(OPEN, new Status[] { ACCEPTED, SCHEDULED, CANCELLED });
		statusChangeMatrix.put(ACCEPTED, new Status[] { INPROGRESS, CANCELLED });
		statusChangeMatrix.put(SCHEDULED, new Status[] { CANCELLED, INPROGRESS });
		statusChangeMatrix.put(INPROGRESS, new Status[] { SEMI_CLOSED, CANCELLED, SCHEDULED });
		statusChangeMatrix.put(SEMI_CLOSED, new Status[] { CLOSED });
	}

	public Order(Long idOrder) {
		this();
		this.idOrder = idOrder;
	}

	public Order(Long idOrder, Date date, Integer status) {
		this();
		this.idOrder = idOrder;
		this.date = date;
		this.status = status;
	}

	public Order(Customer idCustomer, ProfessionalServices professionalServices, Schedule scheduleId) {
		this();
		this.idCustomer = idCustomer;
		this.professionalServices = professionalServices;
		this.scheduleId = scheduleId;
	}

	/**
	 * Antes de atribuir o status, verificamos se a transicao do status antigo pro
	 * novo eh permitida.
	 * 
	 * @param status
	 */
	public void setStatus(Integer statusOrdinal) {
		
		Status statusCandidate = null;
		try {
			statusCandidate = Status.values()[statusOrdinal];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException(statusOrdinal + " nao eh um status valido.");
		}

		
		Status statusCurrent = Status.values()[getStatus()];

		Status[] allowedStatus = this.statusChangeMatrix.get(statusCurrent);

		try {
			Status newStatus = allowedStatus[statusCandidate.ordinal()];
			this.status = newStatus.ordinal();
		} catch (ArrayIndexOutOfBoundsException e) {
			String msg = String.format("Nao eh permitido mudar do status %s para o status %s ou %d eh um ordinal invalido.", statusCurrent,
					statusCandidate);
			
			throw new IllegalStateException(msg);
		}
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (idOrder != null ? idOrder.hashCode() : date != null ? date.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Order)) {
			return false;
		}
		Order other = (Order) object;
		if ((this.idOrder == null && other.idOrder != null)
				|| (this.idOrder != null && !this.idOrder.equals(other.idOrder))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "javaapplication2.entity.Order[ idOrder=" + idOrder + " ]";
	}

}
