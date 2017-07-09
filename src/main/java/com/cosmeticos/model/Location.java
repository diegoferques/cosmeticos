/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author magarrett.dias
 */
@Entity
@Data
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    @Basic(optional = false)
    @Column(name = "coordinate")
    private String coordinate;
    */

    private String latitude;

    private String longitude;

    @Ignore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idLocation")
    private Collection<Order> orderCollection;

    public Location() {
    }

    public Location(Long id) {
        this.id = id;
    }

    @XmlTransient
    public Collection<Order> getSaleCollection() {
        return orderCollection;
    }

    public void setSaleCollection(Collection<Order> orderCollection) {
        this.orderCollection = orderCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Location)) {
            return false;
        }
        Location other = (Location) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Location[ id=" + id + " ]";
    }
    
}
