/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class Hability implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message="Hability name cannot be empty")
    @Column(unique = true)
    private String name;

    @ManyToOne
    private Service service;

    public Hability() {
    }

    public Hability(String name) {
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
        if (!(object instanceof Hability)) {
            return false;
        }
        Hability other = (Hability) object;
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
