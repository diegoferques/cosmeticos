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
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.UserAddImage.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.UserAddImage.class,
    })
    private String cloudUrl;

    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.UserAddImage.class,
    })
    /**
     * @deprecated Verificar ao longo da vida do sistema se precisamos disso.
     */
    private String localUrl;

    @JoinColumn(name = "id_user")
    @ManyToOne(optional = false)
    private User user;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Image)) {
            return false;
        }
        Image other = (Image) object;
        if ((this.id == null && other.id != null)
                || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Image[ idCustomer=" + id + " ]";
    }

}
