/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Sale implements Serializable {

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
    @ManyToOne(optional = true)
    private Location idLocation;

    @JsonBackReference
    @JoinColumn(name = "idCustomer", referencedColumnName = "idCustomer")
    @ManyToOne(optional = false)
    private Customer idCustomer;

    public Sale() {
    }

    public Sale(Long idOrder) {
        this.idOrder = idOrder;
    }

    public Sale(Long idOrder, Date date, Integer status) {
        this.idOrder = idOrder;
        this.date = date;
        this.status = status;
    }

    public Sale(Customer idCustomer, ProfessionalServices professionalServices, Schedule scheduleId) {
        this.idCustomer = idCustomer;
        this.professionalServices = professionalServices;
        this.scheduleId = scheduleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idOrder != null ? idOrder.hashCode() :
                date != null ? date.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sale)) {
            return false;
        }
        Sale other = (Sale) object;
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
