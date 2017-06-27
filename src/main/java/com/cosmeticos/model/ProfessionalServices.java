/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

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
public class ProfessionalServices implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected ProfessionalServicesPK professionalServicesPK;

    @JoinColumn(name = "idService", referencedColumnName = "idService", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Service service;

    @JoinColumn(name = "idProfessional", referencedColumnName = "idProfessional", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Professional professional;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalServices")
    private Collection<Sale> saleCollection;

    public ProfessionalServices() {
    }

    public ProfessionalServices(ProfessionalServicesPK professionalServicesPK) {
        this.professionalServicesPK = professionalServicesPK;
    }

    public ProfessionalServices(long idProfessional, long idService) {
        this.professionalServicesPK = new ProfessionalServicesPK(idProfessional, idService);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (professionalServicesPK != null ? professionalServicesPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProfessionalServices)) {
            return false;
        }
        ProfessionalServices other = (ProfessionalServices) object;
        if ((this.professionalServicesPK == null && other.professionalServicesPK != null) || (this.professionalServicesPK != null && !this.professionalServicesPK.equals(other.professionalServicesPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.ProfessionalServices[ professionalServicesPK=" + professionalServicesPK+ " ]";
    }
    
}
