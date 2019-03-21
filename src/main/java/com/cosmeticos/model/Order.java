/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.cosmeticos.model.Payment.Type.CC;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
@Table(name = "[ORDER]")
public class Order implements Serializable {

	public enum PayType{
    	CASH, CREDITCARD
	}

	public enum AttendanceType {
		HOME_CARE, ON_SITE
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
			ResponseJsonView.OrderControllerFindBy.class,
	})
	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	private AttendanceType attendanceType;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
	@Basic(optional = false)
	@Column(name = "status")
    @Enumerated(EnumType.STRING)
	private OrderStatus status;

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
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @JoinColumn(name = "id_professional_category", referencedColumnName = "professionalCategoryId")
    @ManyToOne(optional = false)
	private ProfessionalCategory professionalCategory;

	@JoinColumn(name = "idLocation", referencedColumnName = "id")
	@ManyToOne(optional = true)
	private Location idLocation;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerUpdate.class,
    })
	@JoinColumn(name = "idCustomer", referencedColumnName = "idCustomer")
	@ManyToOne(optional = false)
	private Customer idCustomer;

	@JsonView({
			ResponseJsonView.OrderControllerCreate.class,
			ResponseJsonView.OrderControllerFindBy.class,
			ResponseJsonView.OrderControllerUpdate.class,
	})
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastStatusUpdate;

    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class
    })
	@Temporal(TemporalType.TIMESTAMP)
	private Date expireTime;

	@JsonView({
			ResponseJsonView.OrderControllerCreate.class,
			ResponseJsonView.OrderControllerUpdate.class,
			ResponseJsonView.OrderControllerFindBy.class
	})
  	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<Payment> paymentCollection = new HashSet<>();


	public Order() {
	}

	public Order(Long idOrder) {
		this();
		this.idOrder = idOrder;
	}

	public Order(Long idOrder, Date date, OrderStatus status) {
		this();
		this.idOrder = idOrder;
		this.date = date;
		this.status = status;
	}

	public Order(Customer idCustomer, ProfessionalCategory professionalCategory, Schedule scheduleId) {
		this();
		this.idCustomer = idCustomer;
		this.professionalCategory = professionalCategory;
		this.scheduleId = scheduleId;
	}

	public boolean isScheduled()
	{
		return this.scheduleId != null;
	}

	public void addPayment(Payment payment) {
		this.paymentCollection.add(payment);
		payment.setOrder(this);
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	/**
	 *
	 * @return TRUE, se a Order for paga no cartao de credito.
	 */
	public boolean isCreditCard()
	{
		Set<Payment> payments = paymentCollection;
		if(payments != null && !payments.isEmpty())
		{
			return payments.stream()
					.filter(p -> CC.equals(p.getType()))
					.findFirst()
					.isPresent();
		}
		return false;
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
		return "javaapplication2.entity.Order[ idOrder=" + String.valueOf(idOrder) + " ]";
	}


}