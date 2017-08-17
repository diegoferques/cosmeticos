/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author magarrett.dias
 */
@Embeddable
public class ProfessionalServicesPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "idProfessional")
    private long idProfessional;

    @Basic(optional = false)
    @Column(name = "idService")
    private long idService;

    public ProfessionalServicesPK() {
    }

    public ProfessionalServicesPK(long idProfessional, long idService) {
        this.idProfessional = idProfessional;
        this.idService = idService;
    }

    public ProfessionalServicesPK(ProfessionalServices ps) {
        this.idProfessional = ps.getProfessional().getIdProfessional();
        this.idService = ps.getCategory().getIdCategory();
    }

    public long getIdProfessional() {
        return idProfessional;
    }

    public void setIdProfessional(long idProfessional) {
        this.idProfessional = idProfessional;
    }

    public long getIdService() {
        return idService;
    }

    public void setIdService(long idService) {
        this.idService = idService;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idProfessional;
        hash += (int) idService;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProfessionalServicesPK)) {
            return false;
        }
        ProfessionalServicesPK other = (ProfessionalServicesPK) object;
        if (this.idProfessional != other.idProfessional) {
            return false;
        }
        if (this.idService != other.idService) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.ProfessionalServicesPK[ idProfessional=" + idProfessional + ", idService=" + idService + " ]";
    }
    
}
