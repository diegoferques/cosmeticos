/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication2.entity;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author magarrett.dias
 */
@Entity
@Table(name = "ServiceRequest")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ServiceRequest.findAll", query = "SELECT s FROM ServiceRequest s"),
    @NamedQuery(name = "ServiceRequest.findByIdServiceRequest", query = "SELECT s FROM ServiceRequest s WHERE s.idServiceRequest = :idServiceRequest"),
    @NamedQuery(name = "ServiceRequest.findByDate", query = "SELECT s FROM ServiceRequest s WHERE s.date = :date"),
    @NamedQuery(name = "ServiceRequest.findByStatus", query = "SELECT s FROM ServiceRequest s WHERE s.status = :status")})
public class ServiceRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idServiceRequest")
    private Long idServiceRequest;
    @Basic(optional = false)
    @Column(name = "date")
    private String date;
    @Basic(optional = false)
    @Column(name = "status")
    private short status;
    @JoinColumn(name = "scheduleId", referencedColumnName = "scheduleId")
    @ManyToOne
    private Schedule scheduleId;
    @JoinColumns({
        @JoinColumn(name = "idProfessional", referencedColumnName = "idProfessional"),
        @JoinColumn(name = "idService", referencedColumnName = "idService")})
    @ManyToOne(optional = false)
    private ProfessionalServices professionalServices;
    @JoinColumn(name = "idLocation", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Location idLocation;
    @JoinColumn(name = "idCustomer", referencedColumnName = "idCustomer")
    @ManyToOne(optional = false)
    private Customer idCustomer;

    public ServiceRequest() {
    }

    public ServiceRequest(Long idServiceRequest) {
        this.idServiceRequest = idServiceRequest;
    }

    public ServiceRequest(Long idServiceRequest, String date, short status) {
        this.idServiceRequest = idServiceRequest;
        this.date = date;
        this.status = status;
    }

    public Long getIdServiceRequest() {
        return idServiceRequest;
    }

    public void setIdServiceRequest(Long idServiceRequest) {
        this.idServiceRequest = idServiceRequest;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Schedule getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Schedule scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ProfessionalServices getProfessionalServices() {
        return professionalServices;
    }

    public void setProfessionalServices(ProfessionalServices professionalServices) {
        this.professionalServices = professionalServices;
    }

    public Location getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Location idLocation) {
        this.idLocation = idLocation;
    }

    public Customer getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Customer idCustomer) {
        this.idCustomer = idCustomer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idServiceRequest != null ? idServiceRequest.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ServiceRequest)) {
            return false;
        }
        ServiceRequest other = (ServiceRequest) object;
        if ((this.idServiceRequest == null && other.idServiceRequest != null) || (this.idServiceRequest != null && !this.idServiceRequest.equals(other.idServiceRequest))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.ServiceRequest[ idServiceRequest=" + idServiceRequest + " ]";
    }
    
}
