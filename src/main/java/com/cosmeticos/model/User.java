/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * @author magarrett.dias
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
// usar NON_EMPTY o jackson considera 0 (credicardCount) como vazio e nao exibe o atributo no json
@Data
@Entity
public class User implements UserDetails {

    public void addVote(Vote v) {
        voteCollection.add(v);
        v.setUser(this);
    }

    public void addImage(Image image) {
        imageCollection.add(image);
        image.setUser(this);
    }


    /**
     * Os tipo sao referentes as classes que tem User. {@link Customer} e {@link Professional}.
     * Manter um enum aqui nesta classe eh mais conveniente.
     */
    public enum Type {
        professional, customer
    }

    public enum Status {

        ACTIVE, INACTIVE, GONE, PENDING_PAYMENT

    }

    public enum PersonType {

        FISICA, JURIDICA

    }

    public enum UserType {

        customer, professional

    }

    private static final long serialVersionUID = 1L;

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class
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
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class
    })
    @Column(unique = true)
    private String email;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class
    })
    @Column(unique = true)
    private String authToken;

    private String sourceApp;

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class
    })
    @Enumerated
    private Status status;

    private String goodByeReason;

    @JsonView({
            ResponseJsonView.WalletsFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private User.Type userType;

    // TODO: role pertence a User, logo, o mappedBy deve ficar aqui e nao em User.
    @JoinTable(name = "UserRoles", joinColumns = {
            @JoinColumn(name = "idLogin", referencedColumnName = "idLogin")}, inverseJoinColumns = {
            @JoinColumn(name = "idRole", referencedColumnName = "idRole")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roleCollection;


    /*
    Sobre o cascade: https://www.mkyong.com/hibernate/cascade-jpa-hibernate-annotation-common-mistake/
     */
    /*@JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })*/
    @JsonIgnore
    // O cartao nunca é retornado nos request ou recebidos. Recebemos o cartao atraves de Payment, no momento da compra da order.
    @Where(clause = "status != 1")
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private Set<CreditCard> creditCardCollection = new HashSet<>();


    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.UserAddImage.class,
    })
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Cascade(CascadeType.ALL)
    private Set<Image> imageCollection = new HashSet<>();

    @JsonBackReference(value = "user-customer")
    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "idUser")
    private Customer customer;

    @JsonBackReference(value = "user-professional")
    @OneToOne(mappedBy = "user")
    @JoinColumn(name = "idUser")
    private Professional professional;


    @NotNull(message = "Tipo de Pessoa nao pode ser nulo pois eh necessario para compras com cartao.")
    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @JsonView({
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    //@Transient
    private Float evaluation = .0f;

    //@JsonView({
    //        ResponseJsonView.CustomerControllerUpdate.class,
    //        ResponseJsonView.CustomerControllerGet.class
    //})
    //@Transient //TODO: resolver o problema do jackson que nao mostra no json se estiver com @Transient, infelizmente gravaremos no banco.
    //private Integer creditCardCount = 0;

    @JsonView({
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    private String profileImageUrl;

    @OneToMany(mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private Set<Vote> voteCollection = new HashSet<>();

    @JsonView({
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastStatusUpdate;

    private String firebaseInstanceId;

    public String getUsername() {
        return email;
    }

    public User() {
    }

    public User(String email, String password) {
        this.password = password;
        this.email = email;
    }

    public void addCreditCard(CreditCard cc) {
        cc.setUser(this);
        this.getCreditCardCollection().add(cc);
    }

    @JsonView({
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class,
            ResponseJsonView.ProfessionalFindAll.class,
    })
    public Integer getCreditCardCount() {
        return creditCardCollection.isEmpty() ? 0 : creditCardCollection.size();
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : getRoleCollection()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
                /*role.getPrivileges().stream()
                        .map(p -> new SimpleGrantedAuthority(p.getName()))
                        .forEach(authorities::add);*/
        }
        return authorities;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idLogin != null ? idLogin.hashCode() :
                email != null ? email.hashCode() : 0);
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
