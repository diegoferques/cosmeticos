package com.cosmeticos.commons;

import com.cosmeticos.model.Customer;
import lombok.Data;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class CustomerResponseBody {

    //private Long idCustomer;

    //private String nameCustomer;
    //private String cpf;
    //private char genre;
    //private String birthDate;
    //private String cellPhone;

    //private String dateRegister;
    //private short status;
    //private User idLogin;
    //private Address idAddress;

    private String description;

    private Customer customer;
}
