
package com.cosmeticos.cielo.model;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CieloCustomer {

    /**
     * Texto 	Opcional    Nome do Comprador.
     */
    public String name;

    /**
     * Texto 	Opcional    Email do Comprador.
     */
    public String email;

    /**
     * Date 	Opcional    Data de nascimento do Comprador.
     */
    public String birthdate;

    public CieloAddress address;

    public DeliveryAddress deliveryAddress;

    /**
     * Texto 	Opcional    Status de cadastro do comprador na loja (NEW / EXISTING)
     */
    private String status;

    /**
     * Texto 	Opcional    Campo de Response. Número do RG, CPF ou CNPJ do Cliente.
     */
    public String identity;

    /**
     * Texto 	Opcional    Campo de Response. Tipo de documento de identificação do comprador (CFP/CNPJ).
     */
    public String identityType;
}
