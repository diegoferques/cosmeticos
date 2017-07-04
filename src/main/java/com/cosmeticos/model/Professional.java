/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.*;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class Professional  implements Serializable {

    public  enum Status
    {
        INACTIVE, ACTIVE;
		
		@JsonValue
		public int toValue() {
			return ordinal();
		}
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
	
    @Enumerated(EnumType.ORDINAL)
    private Status status;

	// TODO incluir @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "idProfessional")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idProfessional")
    private Address address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idProfessional")
    private Wallet wallet;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "professional")
    private Set<ProfessionalServices> professionalServicesCollection = new HashSet<>();

    @JoinTable(name = "PROFESSIONAL_HABILITY", joinColumns = {
            @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional")}, inverseJoinColumns = {
            @JoinColumn(name = "id_hability", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private  Set<Hability> habilityCollection = new HashSet<>();



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
