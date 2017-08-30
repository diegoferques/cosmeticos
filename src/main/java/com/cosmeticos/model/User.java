/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class User implements Serializable {

    public  enum Status {

        ACTIVE, INACTIVE, GONE

    }

    public enum PersonType{

        FISICA, JURIDICA

    }

        private static final long serialVersionUID = 1L;

    @JsonView({
        ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLogin;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @NotEmpty(message = "UserName cannot be empty")
    @Column(unique = true)
    private String username;

    private String password;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    @Column(unique = true)
    private String email;

    private String sourceApp;

    @Enumerated
    private Status status;

    private String goodByeReason;

    @ManyToMany(mappedBy = "userCollection", fetch = FetchType.EAGER)
    private Set<Role> roleCollection;

    /*
    Sobre o cascade: https://www.mkyong.com/hibernate/cascade-jpa-hibernate-annotation-common-mistake/
     */
    @OneToMany(mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<CreditCard> creditCardCollection = new HashSet<>();

    @JsonBackReference(value="user-customer")
    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "idUser")
    private Customer customer;

    @JsonBackReference(value="user-professional")
    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "idUser")
    private Professional professional;


    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @JsonView({
            ResponseJsonView.OrderControllerUpdate.class,
    })
    @Transient
    private float evaluation;

    @OneToMany(mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<Vote> voteCollection = new HashSet<>();



    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

   public void addCreditCard(CreditCard cc)
   {
       cc.setUser(this);
       this.getCreditCardCollection().add(cc);
   }

   //@JsonIgnore // Impede que o password seja mostrado ao se retornar um json no endpoint.
    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLogin != null ? idLogin.hashCode() :
                username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.idLogin == null && other.idLogin != null) || (this.idLogin != null && !this.idLogin.equals(other.idLogin))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.User[ idLogin=" + idLogin + " ]";
    }

}
