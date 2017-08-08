/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class Professional  implements Serializable {

    public enum Type{

        HOME_CARE, ON_SITE, FULL

    }

    public  enum Status
    {
        INACTIVE, ACTIVE, STANDBY, OUT_FOR_SERVICE;
		
		@JsonValue
		public int toValue() {
			return ordinal();
		}
    }

    private static final long serialVersionUID = 1L;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfessional;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    private String nameProfessional;

    @JsonView({
    	
    ResponseJsonView.ProfessionalFindAll.class,
    ResponseJsonView.ProfessionalUpdate.class})
    private String cnpj;

    @JsonView({
    	ResponseJsonView.ProfessionalServicesFindAll.class,
    	ResponseJsonView.ProfessionalFindAll.class,
    	ResponseJsonView.ProfessionalUpdate.class
    })
    private Character genre;

    @JsonView({    	
	    ResponseJsonView.ProfessionalFindAll.class,
	    ResponseJsonView.ProfessionalUpdate.class
    })
    private Date birthDate;

    @JsonView({    	
	    ResponseJsonView.ProfessionalServicesFindAll.class,
	    ResponseJsonView.ProfessionalFindAll.class,
	    ResponseJsonView.ProfessionalUpdate.class
    })
    private String cellPhone;

    private String specialization;

    @JsonView({
    	ResponseJsonView.ProfessionalFindAll.class,
    	ResponseJsonView.ProfessionalUpdate.class
    })
    private String typeService;

    @JsonView({
    	
    ResponseJsonView.ProfessionalServicesFindAll.class,
    ResponseJsonView.ProfessionalFindAll.class,
    ResponseJsonView.ProfessionalUpdate.class})
    private Date dateRegister;
	
    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @JsonView({
            ResponseJsonView.ProfessionalServicesFindAll.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @Enumerated
    private Type attendance;

    @JsonView({
        ResponseJsonView.ProfessionalFindAll.class,
        ResponseJsonView.ProfessionalUpdate.class
    })
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "professional")
    @JoinColumn(name = "idProfessional")
    private Address address;

    /*
       Nao precisamos retornar a carteira de clientes junto com o profissional no json.
       Caso seja necessario, deve ser acessado endpoint wallets/?professional.idProfessional=123
        */
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idProfessional")
    private Wallet wallet;

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "professional")
    private Set<ProfessionalServices> professionalServicesCollection = new HashSet<>();

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @JoinTable(name = "PROFESSIONAL_HABILITY", joinColumns = {
            @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional")}, inverseJoinColumns = {
            @JoinColumn(name = "id_hability", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private  Set<Hability> habilityCollection = new HashSet<>();

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @Transient
    private Long distance;

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    @Transient
    private float evaluation;

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setUser(User user) {
        this.user = user;
    }

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
