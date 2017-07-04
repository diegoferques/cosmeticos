package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vinicius on 30/06/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomerWallet;

    @OneToOne(optional = false)
    //@JoinColumn(name = "idCustomerWallet")
    private Professional professional;

    @JoinTable(name = "CUSTOMER_WALLET", joinColumns = {
            @JoinColumn(name = "id_customerwallet", referencedColumnName = "idCustomerWallet")}, inverseJoinColumns = {
            @JoinColumn(name = "id_customer", referencedColumnName = "idCustomer")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Customer> customerCollection = new ArrayList<>();



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCustomerWallet != null ? idCustomerWallet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wallet)) {
            return false;
        }
        Wallet other = (Wallet) object;
        if ((this.idCustomerWallet == null && other.idCustomerWallet != null) || (this.idCustomerWallet != null && !this.idCustomerWallet.equals(other.idCustomerWallet))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Address[ idCustomer=" + idCustomerWallet + " ]";
    }

}
