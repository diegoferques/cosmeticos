/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPrice;

    @NotEmpty(message="price name cannot be empty")
    private String name;

    @NotNull
    private Long price;

    @JoinColumn(name = "professionalCategory_Id", referencedColumnName = "professionalCategoryId")
    @ManyToOne(optional = false)
    private ProfessionalCategory professionalCategory;

    public PriceRule() {
    }

    public PriceRule(String name) {
        this.name = name;
    }

/*
    /**
     * Nao retornamos esse dado no json.

    @JsonIgnore
    @ManyToMany(mappedBy = "priceCollection")
    private Collection<Professional> professionalCollection = new ArrayList<>();
*/

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPrice != null ? idPrice.hashCode() :

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
        if ((this.idPrice == null && other.idPrice != null) || (this.idPrice != null && !this.idPrice.equals(other.idPrice))) {

                return false;
        }
            return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Hability[ id=" + idPrice + " ]";
    }
    
}
