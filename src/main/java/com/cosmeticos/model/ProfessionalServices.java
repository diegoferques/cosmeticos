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
import javax.validation.constraints.NotNull;

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
    private Long professionalServicesId;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @JoinColumn(name = "id_service", referencedColumnName = "idService")
    @ManyToOne(optional=false)
    private Service service;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional")
    @ManyToOne(optional=false)
    private Professional professional;

    @JsonIgnore // As vendas serao obtidas por endpoint especifico.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalServices")
    private Collection<Order> orderCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professionalCategory")
    private Set<PriceRule> priceRule = new HashSet<>();
    
    @Override
    public String toString() {
        return "javaapplication2.entity.ProfessionalServices[ id=" + professionalServicesId+ " ]";
    }

	public ProfessionalServices(Professional p, Service s) {
		this.professional = p;
		this.service = s;
		
		this.professional.getProfessionalServicesCollection().add(this);
		this.service.getProfessionalServicesCollection().add(this);
		
	}

	public ProfessionalServices() {
	}

}
