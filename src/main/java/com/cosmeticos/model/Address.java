/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAddress;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String address;

    @JsonView({
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String cep;

    private String neighborhood;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String city;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String state;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String country;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String latitude;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String longitude;

    @JsonView({
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    private String complement;

    @JsonIgnore
    @OneToOne
    private Customer customer;

    @JsonIgnore
    @OneToOne
    private Professional professional;
    private String number;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idAddress != null ? idAddress.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Address)) {
            return false;
        }
        Address other = (Address) object;
        if ((this.idAddress == null && other.idAddress != null) || (this.idAddress != null && !this.idAddress.equals(other.idAddress))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Address[ idAddress=" + idAddress + " ]";
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
