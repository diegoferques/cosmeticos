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


    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.OrderControllerUpdate.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCustomer;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @NotEmpty(message = "nameCustomer was not set!")
    private String nameCustomer;

    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @NotEmpty(message = "cpf was not set!")
    private String cpf;

    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private Character genre;

    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })

    @NotNull(message = "birthDate was not set!")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthDate;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private String cellPhone;

    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRegister;

    /**
     * @deprecated Prefira {@link User.Status}
     */
    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private Integer status;

   // @JsonManagedReference(value="user-customer")
    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private User user;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "idCustomer")
    private Address address;

    public void setUser(User user) {
        this.user = user;

        this.user.setUserType(User.Type.customer);
    }

    @JsonIgnore
    @ManyToMany(mappedBy = "customers")
    private Collection<Wallet> wallets = new ArrayList<>();

    @JsonIgnore // Nao tem porque toda vez q retornar um usuario, retornar suas compras.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idCustomer")
    private Collection<Order> orderCollection;

    public User.PersonType getPersonType() {

        if(this.cpf.length() == 11)
            return User.PersonType.FISICA;
        else
            return User.PersonType.JURIDICA;
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
        return "Customer[ idCustomer=" + idCustomer + ", email="+user.getEmail()+" ]";
    }

}
