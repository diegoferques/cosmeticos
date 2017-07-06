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

    public  enum Status
    {
        INACTIVE, ACTIVE;
		
		@JsonValue
		public int toValue() {
			return ordinal();
		}
    }

    private static final long serialVersionUID = 1L;

    @JsonView(ResponseJsonView.ProfessionalServicesFindAll.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProfessional;

    @JsonView(ResponseJsonView.ProfessionalServicesFindAll.class)
    private String nameProfessional;

    private String cnpj;

    private Character genre;

    private Date birthDate;

    private String cellPhone;

    private String specialization;

    private String typeService;

    private Date dateRegister;
	
    @Enumerated(EnumType.ORDINAL)
    private Status status;

	// TODO incluir @NotNull
    @OneToOne(cascade = CascadeType.ALL, optional = false, mappedBy = "professional")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
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

    /**
     *  @JsonBackReference: Professional deve receber mas nao deve retornar esta lista no json do endopoint professionals/.
     * Se a client app deseja saber quais servicos todos os profissionais atendem, ele deve
     * chamar o endpoint professionalservices/
     */
    @JsonBackReference
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "professional")
    private Set<ProfessionalServices> professionalServicesCollection = new HashSet<>();

    @JoinTable(name = "PROFESSIONAL_HABILITY", joinColumns = {
            @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional")}, inverseJoinColumns = {
            @JoinColumn(name = "id_hability", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private  Set<Hability> habilityCollection = new HashSet<>();

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
