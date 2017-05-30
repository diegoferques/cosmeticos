package com.cosmeticos.commons;

import com.cosmeticos.model.Role;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * Created by Lulu on 30/05/2017.
 */
@Data
public class RoleRequestBody {

    @Valid
    private Role entity;
}
