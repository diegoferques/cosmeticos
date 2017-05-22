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
@Table(name = "Log")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l"),
    @NamedQuery(name = "Log.findByIdLog", query = "SELECT l FROM Log l WHERE l.idLog = :idLog"),
    @NamedQuery(name = "Log.findByCustomerEmail", query = "SELECT l FROM Log l WHERE l.customerEmail = :customerEmail"),
    @NamedQuery(name = "Log.findByServiceType", query = "SELECT l FROM Log l WHERE l.serviceType = :serviceType"),
    @NamedQuery(name = "Log.findByProfessionalEmail", query = "SELECT l FROM Log l WHERE l.professionalEmail = :professionalEmail"),
    @NamedQuery(name = "Log.findByEvent", query = "SELECT l FROM Log l WHERE l.event = :event"),
    @NamedQuery(name = "Log.findByDate", query = "SELECT l FROM Log l WHERE l.date = :date")})
public class Log implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idLog")
    private Long idLog;
    @Column(name = "customerEmail")
    private String customerEmail;
    @Column(name = "serviceType")
    private String serviceType;
    @Column(name = "professionalEmail")
    private String professionalEmail;
    @Basic(optional = false)
    @Column(name = "event")
    private short event;
    @Basic(optional = false)
    @Column(name = "date")
    private String date;

    public Log() {
    }

    public Log(Long idLog) {
        this.idLog = idLog;
    }

    public Log(Long idLog, short event, String date) {
        this.idLog = idLog;
        this.event = event;
        this.date = date;
    }

    public Long getIdLog() {
        return idLog;
    }

    public void setIdLog(Long idLog) {
        this.idLog = idLog;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProfessionalEmail() {
        return professionalEmail;
    }

    public void setProfessionalEmail(String professionalEmail) {
        this.professionalEmail = professionalEmail;
    }

    public short getEvent() {
        return event;
    }

    public void setEvent(short event) {
        this.event = event;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLog != null ? idLog.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Log)) {
            return false;
        }
        Log other = (Log) object;
        if ((this.idLog == null && other.idLog != null) || (this.idLog != null && !this.idLog.equals(other.idLog))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Log[ idLog=" + idLog + " ]";
    }
    
}
