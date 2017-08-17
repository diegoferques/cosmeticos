/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
		OPEN, CANCELLED, EXECUTED, SEMI_CLOSED, AUTO_CLOSED, CLOSED, SCHEDULED, INPROGRESS, ACCEPTED, EXPIRED
	}

	private static final long serialVersionUID = 1L;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idOrder;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
	@Basic(optional = false)
	@Column(name = "status")
    @Enumerated(EnumType.STRING)
	private Status status;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
	@JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId")
	@ManyToOne(cascade = CascadeType.ALL)
	private Schedule scheduleId;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @JoinColumns({
        @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional"),
			@JoinColumn(name = "id_service", referencedColumnName = "idService") })
	@ManyToOne(optional = false)
	private ProfessionalServices professionalServices;

	@JoinColumn(name = "idLocation", referencedColumnName = "id")
	@ManyToOne(optional = true)
	private Location idLocation;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
	@JoinColumn(name = "idCustomer", referencedColumnName = "idCustomer")
	@ManyToOne(optional = false)
	private Customer idCustomer;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;

    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class
    })
	@Temporal(TemporalType.TIMESTAMP)
	private Date expireTime;

	private Payment payment;	
	
	public Order() {
	}

	public Order(Long idOrder) {
		this();
		this.idOrder = idOrder;
	}

	public Order(Long idOrder, Date date, Status status) {
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