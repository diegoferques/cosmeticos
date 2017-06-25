package com.cosmeticos.commons;

import com.cosmeticos.model.User;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Vinicius on 29/05/2017.
 */
@Data
public class UserRequestBody {

    @Valid
    private User entity;

}
