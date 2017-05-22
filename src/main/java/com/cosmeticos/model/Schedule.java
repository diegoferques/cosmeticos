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
@Table(name = "Schedule")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Schedule.findAll", query = "SELECT s FROM Schedule s"),
    @NamedQuery(name = "Schedule.findByScheduleId", query = "SELECT s FROM Schedule s WHERE s.scheduleId = :scheduleId"),
    @NamedQuery(name = "Schedule.findByScheduleDate", query = "SELECT s FROM Schedule s WHERE s.scheduleDate = :scheduleDate"),
    @NamedQuery(name = "Schedule.findByStatus", query = "SELECT s FROM Schedule s WHERE s.status = :status")})
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "scheduleId")
    private Long scheduleId;
    @Column(name = "scheduleDate")
    private String scheduleDate;
    @Column(name = "status")
    private Integer status;
    @OneToMany(mappedBy = "scheduleId")
    private Collection<ServiceRequest> serviceRequestCollection;

    public Schedule() {
    }

    public Schedule(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @XmlTransient
    public Collection<ServiceRequest> getServiceRequestCollection() {
        return serviceRequestCollection;
    }

    public void setServiceRequestCollection(Collection<ServiceRequest> serviceRequestCollection) {
        this.serviceRequestCollection = serviceRequestCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (scheduleId != null ? scheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Schedule)) {
            return false;
        }
        Schedule other = (Schedule) object;
        if ((this.scheduleId == null && other.scheduleId != null) || (this.scheduleId != null && !this.scheduleId.equals(other.scheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Schedule[ scheduleId=" + scheduleId + " ]";
    }
    
}
