/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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

    public void addVote(Vote v) {
        voteCollection.add(v);
        v.setUser(this);
    }

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
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLogin;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    //@NotEmpty(message = "UserName cannot be empty")
    @Column(unique = true)
    private String username;

    private String password;

    //TODO: FALTA IMPLEMENTAR ISSO QUANDO UTILIZAR O TOKEN
    //(Voltar pra 0 após utilizar o token para trocar a senha)
    private Boolean lostPassword = false;

    //TOKEN GERADO PARA TROCAR A SENHA
    private String lostPasswordToken;

    //TODO: FALTA IMPLEMENTAR ISSO QUANDO GERA O TOKEN
    //DATA SOLICITADA + X HORAS OU DIAS;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lostPasswordValidThru;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
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
    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class
    })
    @OneToMany(mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<CreditCard> creditCardCollection = new HashSet<>();

    @JsonView({
            ResponseJsonView.CustomerControllerGet.class
    })
    @Transient
    private Integer creditCardCount;

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
            ResponseJsonView.CustomerControllerGet.class
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

    public Integer getCreditCardCount() {
        return creditCardCollection.isEmpty() ? 0 : creditCardCollection.size();
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
