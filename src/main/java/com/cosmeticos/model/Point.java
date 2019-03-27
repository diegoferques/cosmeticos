package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@ToString
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID de {@link User}
     */
    private Long userId;

    private String description;

    /**
     * Recebimento de pontos devem sempre registrar valores positivos e uso de pontos valores negativos.
     */
    private Long value;

    private Long orderId;

    private Date date;
}
