package com.cosmeticos.commons;

import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Lulu on 30/05/2017.
 */
@Data
public class RoleRequestBody {

    @Valid
    private com.cosmeticos.model.Role entity;
}
