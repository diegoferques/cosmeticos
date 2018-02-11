package com.cosmeticos.commons;

import com.cosmeticos.model.Address;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * Created by matto on 09/02/2018.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class AddressResponseBody {

    private String description;

    private List<Address> addresses;

    private Address address;

}
