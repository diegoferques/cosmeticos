package com.cosmeticos.commons;

import com.cosmeticos.model.Role;
import lombok.Data;

/**
 * Created by Lulu on 30/05/2017.
 */
@Data
public class RoleResponseBody {

    private String description;

    private Role role;
}
