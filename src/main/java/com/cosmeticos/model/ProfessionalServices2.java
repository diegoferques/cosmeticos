/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cosmeticos.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.cosmeticos.commons.ResponseJsonView;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 * 
 * TODO: apagar esta classe
 * @deprecated Criada só pra testar se o problema do identitiy era só com a professionalservices com as associaçoes que ela possuia
 * @author magarrett.dias
 */
@Data
@Entity
public class ProfessionalServices2 implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long professionalServicesId;

    @JoinColumn(name = "id_service", referencedColumnName = "idService", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private Service service;

    @JoinColumn(name = "id_professional", referencedColumnName = "idProfessional", insertable = false, updatable = false)
    @ManyToOne(optional = true)
    private Professional professional;
    
    private String name;
}
