/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
	
    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategory;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @NotEmpty(message = "category cannot be empty")
    @Column(unique = true)
    private String name;

	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private Collection<ProfessionalServices> professionalServicesCollection;

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (idCategory != null ? idCategory.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        if ((this.idCategory == null && other.idCategory != null) || (this.idCategory != null && !this.idCategory.equals(other.idCategory))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Category[ idCategory=" + idCategory + " ]";
    }
    
}
