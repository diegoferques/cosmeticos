package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Vinicius on 23/06/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@AllArgsConstructor
@Builder
@Data
@Entity
public class CreditCard implements Serializable {

    public enum Status
    {
        ACTIVE, INACTIVE
    }

    private static final long serialVersionUID = 1l;

    public CreditCard() {
    }

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCreditCard;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class
    })
    private String token;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class
    })
    private String ownerName;

    /**
     * Registraremos os 4 ultimos digitos do cartao
     */
    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @Column(length = 4)
    private String suffix;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private String number;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class
    })
    private String expirationDate;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private String vendor;

    @Setter
    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class
    })
    private Boolean oneClick;

    // TODO: Incluir @Transient do jpa assim q  o problema  entre o jackson e as a notacoes jpq terminarem.
    private String securityCode;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @Enumerated
    private Status status;

    @JoinColumn(name = "id_user")
    @ManyToOne(optional = false)
    private User user;

    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUsage;

/*
    @JsonView({
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.CreditCardFindAll.class,
            ResponseJsonView.CustomerControllerUpdate.class,
            ResponseJsonView.CustomerControllerGet.class
    })
    private Boolean oneClick = false;*/

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "creditCardCollection")
    private Collection<Order> orders = new ArrayList<>();

    public Boolean isOneClick() {
        return oneClick;
    }

    /*@ManyToOne
    private Professional professional;

    @ManyToOne
    private Customer customer;
    */

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idCreditCard != null ? idCreditCard.hashCode() :
                token != null ? token.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CreditCard)) {
            return false;
        }
        CreditCard other = (CreditCard) object;
        if ((this.idCreditCard == null && other.idCreditCard != null) || (this.idCreditCard != null && !this.idCreditCard.equals(other.idCreditCard))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.CreditCardRepository[ idCreditCard=" + idCreditCard + " ]";
    }
}
