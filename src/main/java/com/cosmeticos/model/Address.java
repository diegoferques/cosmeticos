/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.oracle.webservices.internal.api.databinding.DatabindingMode;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
@Table
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAddress;

    private String address;

    private String cep;

    private String neighborhood;

    private String city;

    private String state;

    private String country;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAddress")
    private Collection<Customer> customerCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idAddress")
    private Collection<Professional> professionalCollection;



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAddress != null ? idAddress.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        if ((this.idAddress == null && other.idAddress != null) || (this.idAddress != null && !this.idAddress.equals(other.idAddress))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Address[ idAddress=" + idAddress + " ]";
    }
    
}
