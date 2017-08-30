package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Vinicius on 30/06/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class Wallet implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Visibility {
		PUBLIC, PRIVATE
	}

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idWallet;

    @OneToOne(optional = false)
    private Professional professional;

    @JsonView(ResponseJsonView.WalletsFindAll.class)
    @JoinTable(name = "CUSTOMER_WALLET", joinColumns = {
            @JoinColumn(name = "id_wallet", referencedColumnName = "idWallet")}, inverseJoinColumns = {
            @JoinColumn(name = "id_customer", referencedColumnName = "idCustomer")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Customer> customers = new ArrayList<>();
    
    private Visibility visibility;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idWallet != null ? idWallet.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Wallet)) {
            return false;
        }
        Wallet other = (Wallet) object;
        if ((this.idWallet == null && other.idWallet != null) || (this.idWallet != null && !this.idWallet.equals(other.idWallet))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Wallet[ idWallet=" + idWallet + " ]";
    }

}
