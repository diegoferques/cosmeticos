/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class PriceRule implements Serializable {
    private static final long serialVersionUID = 1L;


    @JsonView
            ({
                    ResponseJsonView.OrderControllerCreate.class,
                    ResponseJsonView.OrderControllerUpdate.class,
                    ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalCategoryFindAll.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message="price name cannot be empty")
    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalCategoryFindAll.class,
    })
    private String name;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalCategoryFindAll.class,
    })
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

    public PriceRule(String name, long price) {
        this.name = name;
        this.price = price;
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
        return "javaapplication2.entity.Hability[ idPrice=" + id + " ]";
    }
    
}
