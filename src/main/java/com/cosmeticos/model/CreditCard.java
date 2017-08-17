package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Vinicius on 23/06/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Entity
public class CreditCard implements Serializable {

    public enum Status
    {
        ACTIVE, INACTIVE
    }

    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCreditCard;

    private String token;

    private String ownerName;

    // TODO: Eliminar, nao podemos ter isso registrado.
    @Column(unique = true)
    private String cardNumber;

    // TODO: Eliminar, nao podemos ter isso registrado.
    private String securityCode;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expirationDate;

    private String vendor;

    @Enumerated
    private Status status;

    @JsonBackReference(value="user-cc")
    @JoinColumn(name = "id_user")
    @ManyToOne(optional = false)
    private User user;

    @JsonView({
            ResponseJsonView.OrderControllerFindBy.class
    })
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUsage;

    @JoinColumn(name = "id_Order")
    @ManyToOne(optional = false)
    private Order order;

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
