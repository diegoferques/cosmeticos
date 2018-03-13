package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class BalanceItem {
    public enum Type { CREDIT, WITHDRALL;}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String description;

    private Long value;

    private Long orderId;

    private Type type;

    private Date date;
}
