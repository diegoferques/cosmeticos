package com.cosmeticos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * Created by matto on 31/07/2017.
 */
@Data
@Entity
public class Vote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVote;

    @JsonIgnore
    @ManyToOne
    private User user;

    @Min(0)
    @Max(5)
    private Integer value;

    public Vote() {
    }

    public Vote(Integer value) {
        this.value = value;
    }
}
