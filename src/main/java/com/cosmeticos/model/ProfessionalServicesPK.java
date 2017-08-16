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
    @Column(name = "idCategory")
    private long idCategory;

    public ProfessionalServicesPK() {
    }

    public ProfessionalServicesPK(long idProfessional, long idCategory) {
        this.idProfessional = idProfessional;
        this.idCategory = idCategory;
    }

    public ProfessionalServicesPK(ProfessionalServices ps) {
        this.idProfessional = ps.getProfessional().getIdProfessional();
        this.idCategory = ps.getCategory().getIdCategory();
    }

    public long getIdProfessional() {
        return idProfessional;
    }

    public void setIdProfessional(long idProfessional) {
        this.idProfessional = idProfessional;
    }

    public long getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(long idCategory) {
        this.idCategory = idCategory;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idProfessional;
        hash += (int) idCategory;
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
        if (this.idCategory != other.idCategory) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.ProfessionalServicesPK[ idProfessional=" + idProfessional + ", idCategory=" + idCategory + " ]";
    }
    
}
