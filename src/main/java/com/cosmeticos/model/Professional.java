/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class Professional implements Serializable {

    public  enum Status
    {
        INACTIVE, ACTIVE
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfessional;

    private String nameProfessional;

    private String cnpj;

    private char genre;

    private Date birthDate;

    private String cellPhone;

    private String specialization;

    private String typeService;

    private Date dateRegister;

    private Status status;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "idProfessional")
    private User idLogin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idProfessional")
    private Address idAddress;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professional")
    private Collection<ProfessionalServices> professionalServicesCollection;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProfessional != null ? idProfessional.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Professional)) {
            return false;
        }
        Professional other = (Professional) object;
        if ((this.idProfessional == null && other.idProfessional != null) || (this.idProfessional != null && !this.idProfessional.equals(other.idProfessional))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Professional[ idProfessional=" + idProfessional + " ]";
    }
    
}
