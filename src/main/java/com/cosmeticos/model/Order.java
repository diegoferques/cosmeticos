/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
@Table
public class Order implements Serializable {

    public enum Status {
        CREATED, ABORTED, EXECUTED
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;

    //@Basic(optional = false)
    //@Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Basic(optional = false)
    @Column(name = "status")
    private Integer status;

    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId")
    @ManyToOne
    private Schedule scheduleId;

    @JoinColumns({
        @JoinColumn(name = "idProfessional", referencedColumnName = "idProfessional"),
        @JoinColumn(name = "idService", referencedColumnName = "idService")})
    @ManyToOne(optional = false)
    private ProfessionalServices professionalServices;

    @JoinColumn(name = "idLocation", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Location idLocation;

    @JoinColumn(name = "idCustomer", referencedColumnName = "idCustomer")
    @ManyToOne(optional = false)
    private Customer idCustomer;

    public Order() {
    }

    public Order(Long idOrder) {
        this.idOrder = idOrder;
    }

    public Order(Long idOrder, Date date, Integer status) {
        this.idOrder = idOrder;
        this.date = date;
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOrder != null ? idOrder.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Order)) {
            return false;
        }
        Order other = (Order) object;
        if ((this.idOrder == null && other.idOrder != null) || (this.idOrder != null && !this.idOrder.equals(other.idOrder))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Order[ idOrder=" + idOrder + " ]";
    }
    
}
