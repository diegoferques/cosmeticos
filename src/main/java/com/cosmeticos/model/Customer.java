/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author magarrett.dias
 */
@Data
@Entity
public class Customer implements Serializable {

    public enum Status {
        INACTIVE, ACTIVE
    }

    private static final long serialVersionUID = 1L;


    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomer;

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @NotEmpty(message = "nameCustomer was not set!")
    private String nameCustomer;

    @NotEmpty(message = "cpf was not set!")
    private String cpf;

    private char genre;

    //TODO - Troquei o TemporalType de TIMESTAMP para DATE, verificar se essa alteração não tem problema
    //Voltei para TimeStamp
    @NotNull(message = "birthDate was not set!")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    private String cellPhone;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRegister;

    private Integer status;

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "idCustomer")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idCustomer")
    private Address idAddress;

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "customers")
    private Collection<Wallet> wallets = new ArrayList<>();

    @JsonIgnore // Nao tem porque toda vez q retornar um usuario, retornar suas compras.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCustomer")
    private Collection<Order> orderCollection;


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
