
package com.cosmeticos.cielo.model;


import lombok.Data;

@Data
public class DeliveryAddress {

    public String street;
    public String number;
    public String complement;
    public String zipCode;
    public String city;
    public String state;
    public String country;

}
