package com.cosmeticos.commons;

import com.cosmeticos.model.Address;
import com.cosmeticos.model.Customer;
import com.cosmeticos.model.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Date;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class CustomerRequestBody {
/*
    //private Long idCustomer;
    @NotEmpty(message = "nameCustomer was not set!")
    private String nameCustomer;

    @NotEmpty(message = "cpf was not set!")
    private String cpf;

    //@NotEmpty(message = "genre was not set!")
    private char genre;

    @NotEmpty(message = "birthDate was not set!")
    private Date birthDate;

    @NotEmpty(message = "cellPhone was not set!")
    private String cellPhone;
*/

    // TODO deletar os campos acima
    private Address address;

    private User user;

    private Customer customer;
}
