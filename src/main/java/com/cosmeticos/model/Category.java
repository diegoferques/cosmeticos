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
import java.util.HashSet;
import java.util.Set;

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
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCategory;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @NotEmpty(message = "category cannot be empty")
    @Column(unique = true)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ownerCategory_id")
    private Category ownerCategory;


    @OneToMany(mappedBy = "ownerCategory", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Category> childrenCategories = new HashSet<>();

	@JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "category")
    private Collection<ProfessionalServices> professionalServicesCollection;

    public void addChild(Category s) {
        getChildrenCategories().add(s);
        s.setOwnerCategory(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCategory != null ? idCategory.hashCode() : name != null ? name.hashCode() : 0);
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
