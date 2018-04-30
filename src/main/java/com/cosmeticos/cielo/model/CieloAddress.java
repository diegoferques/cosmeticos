
package com.cosmeticos.cielo.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CieloAddress {

    public String street;
    public String number;
    public String complement;
    public String zipCode;
    public String city;
    public String state;
    public String country;

}
