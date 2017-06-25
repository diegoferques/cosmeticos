/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idService;

    @NotEmpty(message = "category cannot be empty")
    @Column(unique = true)
    private String category;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<ProfessionalServices> professionalServicesCollection;


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idService != null ? idService.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.idService == null && other.idService != null) || (this.idService != null && !this.idService.equals(other.idService))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Service[ idService=" + idService + " ]";
    }
    
}
