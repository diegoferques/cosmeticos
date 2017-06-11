/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
public class Customer implements Serializable {

    public  enum Status
    {
        INACTIVE, ACTIVE
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomer;

    @NotEmpty(message = "nameCustomer was not set!")
    private String nameCustomer;

    private String cpf;

    private char genre;

    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    private String cellPhone;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRegister;

    private Integer status;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "idCustomer")
    private User idLogin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idCustomer")
    private Address idAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCustomer")
    private Collection<ServiceRequest> serviceRequestCollection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCustomer != null ? idCustomer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.idCustomer == null && other.idCustomer != null) || (this.idCustomer != null && !this.idCustomer.equals(other.idCustomer))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Customer[ idCustomer=" + idCustomer + " ]";
    }
    
}
