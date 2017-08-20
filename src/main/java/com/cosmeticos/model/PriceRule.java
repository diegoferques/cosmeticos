/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class PriceRule implements Serializable {
    private static final long serialVersionUID = 1L;


    @JsonView({
    	ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalServicesFindAll.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView({
    	ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalServicesFindAll.class,
    })
    @NotEmpty(message="Hability name cannot be empty")
    private String name;

    @JsonView({
    	ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalServicesFindAll.class,
    })
    @NotNull
    private Long price;
    
    @ManyToOne
    private ProfessionalServices professionalCategory;

    public PriceRule() {
    }

    public PriceRule(String name) {
        this.name = name;
    }


    /**
     * Nao retornamos esse dado no json.
     */
    @JsonIgnore
    @ManyToMany(mappedBy = "habilityCollection")
    private Collection<Professional> professionalCollection = new ArrayList<>();

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() :

                // Name eh unique, portanto nao preciso ter medo de usa-lo como hash.
                name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PriceRule)) {
            return false;
        }
        PriceRule other = (PriceRule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {

                return false;
        }
            return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Hability[ id=" + id + " ]";
    }
    
}
