/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class ProfessionalCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        ACTIVE, DELETED
    }

    @JsonView({
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professionalCategoryId;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @JsonView({
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @JoinColumn(name = "id_category", referencedColumnName = "idCategory")
    @ManyToOne(optional=false)
    private Category category;

    @JsonView({
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CategoryGetAll.class,
    })
    @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional")
    @ManyToOne(optional=false)
    private Professional professional;

    @JsonIgnore // As vendas serao obtidas por endpoint especifico.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalCategory")
    private Collection<Order> orderCollection;

    @JsonView({
    	ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalCategoryFindAll.class,
        ResponseJsonView.ProfessionalFindAll.class,
        ResponseJsonView.ProfessionalUpdate.class
    })
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalCategory", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PriceRule> priceRuleList = new HashSet<>();


    @Override
    public String toString() {
        return "javaapplication2.entity.ProfessionalCategory[ id=" + professionalCategoryId+ " ]";
    }

    public ProfessionalCategory(Professional p, Category s) {
        this.professional = p;
        this.category = s;

        this.professional.getProfessionalCategoryCollection().add(this);
        this.category.getProfessionalCategoryCollection().add(this);


    }



	public ProfessionalCategory() {
	}

	public void addPriceRule(PriceRule pr1) {
		pr1.setProfessionalCategory(this);
        priceRuleList.add(pr1);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (professionalCategoryId != null ? professionalCategoryId.hashCode() : category != null ? category.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProfessionalCategory)) {
            return false;
        }
        ProfessionalCategory other = (ProfessionalCategory) object;
        if ((this.professionalCategoryId == null && other.professionalCategoryId != null)
                || (this.professionalCategoryId != null && !this.professionalCategoryId.equals(other.professionalCategoryId))) {
            return false;
        }
        return true;
    }

}
