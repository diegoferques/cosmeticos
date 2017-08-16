package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

/**
 * Created by Vinicius on 15/08/2017.
 */
@Data
@Entity
public class CategoryPrice {

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
    private Long price;


    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @JoinColumn(name = "idCategory", referencedColumnName = "idCategory", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Category category;

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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoryPrice")
    private Collection<Order> orderCollection;

    private String rule;

    public CategoryPrice() {
    }

    public CategoryPrice(ProfessionalServicesPK professionalServicesPK) {
        this.professionalServicesPK = professionalServicesPK;
    }

    public CategoryPrice(long idProfessional, long idCategory) {
        this.professionalServicesPK = new ProfessionalServicesPK(idProfessional, idCategory);
    }

    public CategoryPrice(Professional professional, Category category) {
        this.professionalServicesPK = new ProfessionalServicesPK(
                professional.getIdProfessional(),
                category.getIdCategory());

        this.professional = professional;
        this.category = category;
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
