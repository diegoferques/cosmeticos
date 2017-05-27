package com.cosmeticos.commons;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class CustomerRequestBody {

    //private Long idCustomer;
    @NotEmpty(message = "nameCustomer was not set!")
    private String nameCustomer;

    @NotEmpty(message = "cpf was not set!")
    private String cpf;

    //@NotEmpty(message = "genre was not set!")
    private char genre;

    @NotEmpty(message = "birthDate was not set!")
    private String birthDate;

    @NotEmpty(message = "cellPhone was not set!")
    private String cellPhone;

    //private String dateRegister;
    //private short status;
    //private User idLogin;
    //private Address idAddress;


}
