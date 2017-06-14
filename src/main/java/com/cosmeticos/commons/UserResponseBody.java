package com.cosmeticos.commons;

import com.cosmeticos.model.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 30/05/2017.
 */
@Data
public class UserResponseBody {

    private String description;

    private List<User> userList = new ArrayList<>(10);

}
