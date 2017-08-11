/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class ProfessionalServices implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore // Nao precisamos exibir este atributo pro cliente.
    @EmbeddedId
    protected ProfessionalServicesPK professionalServicesPK;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @JoinColumn(name = "idService", referencedColumnName = "idService", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Service service;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @JoinColumn(name = "idProfessional", referencedColumnName = "idProfessional", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Professional professional;

    @JsonIgnore // As vendas serao obtidas por endpoint especifico.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalServices")
    private Collection<Order> orderCollection;

    public ProfessionalServices() {
    }

    public ProfessionalServices(ProfessionalServicesPK professionalServicesPK) {
        this.professionalServicesPK = professionalServicesPK;
    }

    public ProfessionalServices(long idProfessional, long idService) {
        this.professionalServicesPK = new ProfessionalServicesPK(idProfessional, idService);
    }

    public ProfessionalServices(Professional professional, Service service) {
        this.professionalServicesPK = new ProfessionalServicesPK(
                professional.getIdProfessional(),
                service.getIdService());

        this.professional = professional;
        this.service = service;
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
