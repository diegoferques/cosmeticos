/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
//@SequenceGenerator(name = "myseq", sequenceName = "MY_SEQ", initialValue = 1, allocationSize = 1)
public class ProfessionalServices implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="myseq")
    private Long professionalCategoryId;

	@JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @JoinColumn(name = "id_category", referencedColumnName = "idCategory")
    @ManyToOne(optional=false)
    private Category category;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
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
        ResponseJsonView.ProfessionalServicesFindAll.class,
    })
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalCategory")
    private Set<PriceRule> priceRuleList = new HashSet<>();

	@Override
    public String toString() {
        return "javaapplication2.entity.ProfessionalServices[ id=" + professionalCategoryId+ " ]";
    }

	public ProfessionalServices(Professional p, Category s) {
		this.professional = p;
		this.category = s;

		this.professional.getProfessionalServicesCollection().add(this);
		this.category.getProfessionalServicesCollection().add(this);

	}

	public ProfessionalServices() {
	}

	public void addPriceRule(PriceRule pr1) {
		priceRuleList.add(pr1);
		pr1.setProfessionalCategory(this);
	}

}
