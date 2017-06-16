package com.cosmeticos.commons;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import lombok.Data;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class ProfessionalRequestBody {

    private Address address;

    private User user;

    private Professional customer;
}
