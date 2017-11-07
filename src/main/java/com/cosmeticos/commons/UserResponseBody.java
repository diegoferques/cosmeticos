package com.cosmeticos.commons;

import com.cosmeticos.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 30/05/2017.
 */
@Data
public class UserResponseBody {

    @JsonView(
            {
                    ResponseJsonView.ProfessionalCategoryFindAll.class
            })
    private String description;

    @JsonView(
            {
                    ResponseJsonView.ProfessionalCategoryFindAll.class
            })
    private List<User> userList = new ArrayList<>(10);

    public UserResponseBody() {
    }

    public UserResponseBody(User user) {
        this.userList.add(user);
    }
}
