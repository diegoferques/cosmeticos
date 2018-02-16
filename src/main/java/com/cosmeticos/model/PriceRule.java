/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
public class PriceRule implements Serializable {
    private static final long serialVersionUID = 1L;


    @JsonView
            ({
                    ResponseJsonView.OrderControllerCreate.class,
                    ResponseJsonView.OrderControllerUpdate.class,
                    ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalCategoryFindAll.class,
                    ResponseJsonView.ProfessionalFindAll.class,
                    ResponseJsonView.ProfessionalUpdate.class
    })
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message="Price.name cannot be empty")
    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
        ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    private String name;

    @JsonView({
            ResponseJsonView.OrderControllerCreate.class,
            ResponseJsonView.OrderControllerUpdate.class,
            ResponseJsonView.OrderControllerFindBy.class,
            ResponseJsonView.ProfessionalCategoryFindAll.class,
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class
    })
    private Long price;

    @JoinColumn(name = "professionalCategory_Id", referencedColumnName = "professionalCategoryId")
    @ManyToOne(optional = false)
    private ProfessionalCategory professionalCategory;

    /**
     * Eh opcional pois nao depende de paymentCollection pra existir. Por exemplo: profissional cadastra regra de preco mas
     * essa regra so estara associada a um paymentCollection quando o cliente desejar fazer um pedido.
     *
     * https://stackoverflow.com/a/36540371/3810036 Diz que paymentCollection nao pode ser opcional. Mas paymentCollection eh opciional, pq
     * a pricerule eh criada pelo profissional no momento do cadastro,e no cadastro nao ha paymentCollection.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "priceRule")
    private Set<Payment> paymentCollection = new HashSet<>();

    public PriceRule() {
    }

    public PriceRule(String name) {
        this.name = name;
    }

    public PriceRule(String name, long price) {
        this.name = name;
        this.price = price;
    }

    public void addPayment(Payment payment)
    {
        paymentCollection.add(payment);
        payment.setPriceRule(this);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() :

                // Name eh unique, portanto nao preciso ter medo de usa-lo como hash.
                name != null ? name.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PriceRule)) {
            return false;
        }
        PriceRule other = (PriceRule) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {

                return false;
        }
            return true;
    }

    @Override
    public String toString() {
        return "javaapplication2.entity.Hability[ idPrice=" + id + " ]";
    }
    
}
