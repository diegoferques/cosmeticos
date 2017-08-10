package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

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

    private String cardNumber;

    private String securityCode;

    //TODO - VALIDADE DO CARTAO DEVERIA SER STRING (EX.: "07/2022")
    @Size(message = "expirationDate must have 7 characters! (EX.: MM/YYYY)", min = 7, max = 7)
    private String expirationDate;

    private String vendor;

    @Enumerated
    private Status status;

    @JsonBackReference(value="user-cc")
    @JoinColumn(name = "id_user")
    @ManyToOne(optional = false)
    private User user;



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
