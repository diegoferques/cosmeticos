package com.cosmeticos.commons;

import com.cosmeticos.model.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lulu on 30/05/2017.
 */
@Data
public class RoleResponseBody {

    private String description;

    private List<Role> roleList= new ArrayList<>(10);
}
