package com.cosmeticos.model;

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
public class CustomerWallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomerWallet;

    @OneToOne
    private Professional professional;

    //@JoinColumn(name = "customerWallets", referencedColumnName = "customerWallets")
    @ManyToMany
    private Set<Customer> customerCollection = new HashSet<>();



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCustomerWallet != null ? idCustomerWallet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CustomerWallet)) {
            return false;
        }
        CustomerWallet other = (CustomerWallet) object;
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
