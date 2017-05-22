/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

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
@Table(name = "Customer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c"),
    @NamedQuery(name = "Customer.findByIdCustomer", query = "SELECT c FROM Customer c WHERE c.idCustomer = :idCustomer"),
    @NamedQuery(name = "Customer.findByNameCustomer", query = "SELECT c FROM Customer c WHERE c.nameCustomer = :nameCustomer"),
    @NamedQuery(name = "Customer.findByCpf", query = "SELECT c FROM Customer c WHERE c.cpf = :cpf"),
    @NamedQuery(name = "Customer.findByGenre", query = "SELECT c FROM Customer c WHERE c.genre = :genre"),
    @NamedQuery(name = "Customer.findByBirthDate", query = "SELECT c FROM Customer c WHERE c.birthDate = :birthDate"),
    @NamedQuery(name = "Customer.findByCellPhone", query = "SELECT c FROM Customer c WHERE c.cellPhone = :cellPhone"),
    @NamedQuery(name = "Customer.findByDateRegister", query = "SELECT c FROM Customer c WHERE c.dateRegister = :dateRegister"),
    @NamedQuery(name = "Customer.findByStatus", query = "SELECT c FROM Customer c WHERE c.status = :status")})
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "idCustomer")
    private Long idCustomer;
    @Basic(optional = false)
    @Column(name = "nameCustomer")
    private String nameCustomer;
    @Basic(optional = false)
    @Column(name = "cpf")
    private String cpf;
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
    @Column(name = "date_register")
    private String dateRegister;
    @Basic(optional = false)
    @Column(name = "status")
    private short status;
    @JoinColumn(name = "idLogin", referencedColumnName = "idLogin")
    @ManyToOne(optional = false)
    private User idLogin;
    @JoinColumn(name = "idAddress", referencedColumnName = "idAddress")
    @ManyToOne(optional = false)
    private Address idAddress;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCustomer")
    private Collection<ServiceRequest> serviceRequestCollection;

    public Customer() {
    }

    public Customer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Customer(Long idCustomer, String nameCustomer, String cpf, char genre, String birthDate, String cellPhone, String dateRegister, short status) {
        this.idCustomer = idCustomer;
        this.nameCustomer = nameCustomer;
        this.cpf = cpf;
        this.genre = genre;
        this.birthDate = birthDate;
        this.cellPhone = cellPhone;
        this.dateRegister = dateRegister;
        this.status = status;
    }

    public Long getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(Long idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getNameCustomer() {
        return nameCustomer;
    }

    public void setNameCustomer(String nameCustomer) {
        this.nameCustomer = nameCustomer;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public String getDateRegister() {
        return dateRegister;
    }

    public void setDateRegister(String dateRegister) {
        this.dateRegister = dateRegister;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
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
    public Collection<ServiceRequest> getServiceRequestCollection() {
        return serviceRequestCollection;
    }

    public void setServiceRequestCollection(Collection<ServiceRequest> serviceRequestCollection) {
        this.serviceRequestCollection = serviceRequestCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCustomer != null ? idCustomer.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.idCustomer == null && other.idCustomer != null) || (this.idCustomer != null && !this.idCustomer.equals(other.idCustomer))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Customer[ idCustomer=" + idCustomer + " ]";
    }
    
}
