package com.cosmeticos.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Vinicius on 17/04/2018.
 */
@Data
@Entity
public class OrderProperty implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String value;

    @JoinColumn(name = "id_order", referencedColumnName = "idOrder")
    @ManyToOne(optional = false)
    private Order order;


}
