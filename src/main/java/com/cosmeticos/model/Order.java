/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author magarrett.dias
 */
@Entity
@Table
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrder;
    @Basic(optional = false)
    @Column(name = "date")
    private String date;
    @Basic(optional = false)
    @Column(name = "status")
    private short status;
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

    public Order(Long idOrder, String date, short status) {
        this.idOrder = idOrder;
        this.date = date;
        this.status = status;
    }

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Schedule getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Schedule scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ProfessionalServices getProfessionalServices() {
        return professionalServices;
    }

    public void setProfessionalServices(ProfessionalServices professionalServices) {
        this.professionalServices = professionalServices;
    }

    public Location getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Location idLocation) {
        this.idLocation = idLocation;
    }

    public Customer getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Customer idCustomer) {
        this.idCustomer = idCustomer;
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
