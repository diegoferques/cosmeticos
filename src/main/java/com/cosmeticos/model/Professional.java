/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2.entity;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author magarrett.dias
 */
@Entity
@Table(name = "Professional")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Professional.findAll", query = "SELECT p FROM Professional p"),
    @NamedQuery(name = "Professional.findByIdProfessional", query = "SELECT p FROM Professional p WHERE p.idProfessional = :idProfessional"),
    @NamedQuery(name = "Professional.findByNameProfessional", query = "SELECT p FROM Professional p WHERE p.nameProfessional = :nameProfessional"),
    @NamedQuery(name = "Professional.findByCnpj", query = "SELECT p FROM Professional p WHERE p.cnpj = :cnpj"),
    @NamedQuery(name = "Professional.findByGenre", query = "SELECT p FROM Professional p WHERE p.genre = :genre"),
    @NamedQuery(name = "Professional.findByBirthDate", query = "SELECT p FROM Professional p WHERE p.birthDate = :birthDate"),
    @NamedQuery(name = "Professional.findByCellPhone", query = "SELECT p FROM Professional p WHERE p.cellPhone = :cellPhone"),
    @NamedQuery(name = "Professional.findBySpecialization", query = "SELECT p FROM Professional p WHERE p.specialization = :specialization"),
    @NamedQuery(name = "Professional.findByTypeService", query = "SELECT p FROM Professional p WHERE p.typeService = :typeService"),
    @NamedQuery(name = "Professional.findByDateRegister", query = "SELECT p FROM Professional p WHERE p.dateRegister = :dateRegister")})
public class Professional implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idProfessional")
    private Long idProfessional;
    @Basic(optional = false)
    @Column(name = "nameProfessional")
    private String nameProfessional;
    @Basic(optional = false)
    @Column(name = "cnpj")
    private String cnpj;
    @Basic(optional = false)
    @Column(name = "genre")
    private char genre;
    @Basic(optional = false)
    @Column(name = "birth_date")
    private String birthDate;
    @Basic(optional = false)
    @Column(name = "cellPhone")
    private String cellPhone;
    @Basic(optional = false)
    @Column(name = "specialization")
    private String specialization;
    @Basic(optional = false)
    @Column(name = "type_service")
    private String typeService;
    @Basic(optional = false)
    @Column(name = "date_register")
    private String dateRegister;
    @JoinColumn(name = "idLogin", referencedColumnName = "idLogin")
    @ManyToOne(optional = false)
    private User idLogin;
    @JoinColumn(name = "idAddress", referencedColumnName = "idAddress")
    @ManyToOne(optional = false)
    private Address idAddress;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "professional")
    private Collection<ProfessionalServices> professionalServicesCollection;

    public Professional() {
    }

    public Professional(Long idProfessional) {
        this.idProfessional = idProfessional;
    }

    public Professional(Long idProfessional, String nameProfessional, String cnpj, char genre, String birthDate, String cellPhone, String specialization, String typeService, String dateRegister) {
        this.idProfessional = idProfessional;
        this.nameProfessional = nameProfessional;
        this.cnpj = cnpj;
        this.genre = genre;
        this.birthDate = birthDate;
        this.cellPhone = cellPhone;
        this.specialization = specialization;
        this.typeService = typeService;
        this.dateRegister = dateRegister;
    }

    public Long getIdProfessional() {
        return idProfessional;
    }

    public void setIdProfessional(Long idProfessional) {
        this.idProfessional = idProfessional;
    }

    public String getNameProfessional() {
        return nameProfessional;
    }

    public void setNameProfessional(String nameProfessional) {
        this.nameProfessional = nameProfessional;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public char getGenre() {
        return genre;
    }

    public void setGenre(char genre) {
        this.genre = genre;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getTypeService() {
        return typeService;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public User getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(User idLogin) {
        this.idLogin = idLogin;
    }

    public Address getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(Address idAddress) {
        this.idAddress = idAddress;
    }

    @XmlTransient
    public Collection<ProfessionalServices> getProfessionalServicesCollection() {
        return professionalServicesCollection;
    }

    public void setProfessionalServicesCollection(Collection<ProfessionalServices> professionalServicesCollection) {
        this.professionalServicesCollection = professionalServicesCollection;
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
