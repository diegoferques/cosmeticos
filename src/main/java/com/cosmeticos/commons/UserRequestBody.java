package com.cosmeticos.commons;

import com.cosmeticos.model.User;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Vinicius on 29/05/2017.
 */
@Data
public class UserRequestBody {

    private Long idUser;

    @NotEmpty
    private String ownerName;
    @NotNull
    private String passWordUser;

    private String sourceApp;

    public UserRequestBody(){

    }
    public UserRequestBody(User user){

    }
}
