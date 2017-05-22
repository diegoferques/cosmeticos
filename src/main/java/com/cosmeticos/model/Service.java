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
@Table(name = "Service")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Service.findAll", query = "SELECT s FROM Service s"),
    @NamedQuery(name = "Service.findByIdService", query = "SELECT s FROM Service s WHERE s.idService = :idService"),
    @NamedQuery(name = "Service.findByCategory", query = "SELECT s FROM Service s WHERE s.category = :category")})
public class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idService")
    private Long idService;
    @Basic(optional = false)
    @Column(name = "category")
    private String category;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "service")
    private Collection<ProfessionalServices> professionalServicesCollection;

    public Service() {
    }

    public Service(Long idService) {
        this.idService = idService;
    }

    public Service(Long idService, String category) {
        this.idService = idService;
        this.category = category;
    }

    public Long getIdService() {
        return idService;
    }

    public void setIdService(Long idService) {
        this.idService = idService;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
        hash += (idService != null ? idService.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.idService == null && other.idService != null) || (this.idService != null && !this.idService.equals(other.idService))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Service[ idService=" + idService + " ]";
    }
    
}
