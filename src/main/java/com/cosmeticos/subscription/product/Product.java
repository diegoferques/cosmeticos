package com.cosmeticos.subscription.product;

//import com.cosmeticos.subscription.Pricing;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Vinicius on 16/07/2017.
 */
@Data
@Entity
@Table(name = "[PRODUCT]")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;


    public enum statusProduct {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProduct;

    @NotEmpty(message = "nameProduct was not set!")
    private String nameProduct;

    @NotEmpty(message = "descriptionProduct was not set!")
    private String descriptionProduct;

    @Enumerated
    private statusProduct statusProduct;

   /* @OneToMany(mappedBy = "user")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<Pricing> creditCardCollection = new HashSet<>();
    */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product)) {
            return false;
        }
        Product other = (Product) o;
        if ((this.idProduct == null && other.idProduct != null) || (this.idProduct != null && !this.idProduct.equals(other.idProduct))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idProduct != null ? idProduct.hashCode() :
                descriptionProduct != null ? descriptionProduct.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Order[ idProduct=" + idProduct + " ]";
    }
}
