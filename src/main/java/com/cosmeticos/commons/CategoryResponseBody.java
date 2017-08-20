package com.cosmeticos.commons;

import java.util.ArrayList;
import java.util.List;

import com.cosmeticos.model.Category;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Data
public class CategoryResponseBody {

    @JsonView({
            ResponseJsonView.CategoryGetAll.class,
    })
    private String description;

    @JsonView({
            ResponseJsonView.CategoryGetAll.class,
    })
    private List<Category> categoryList = new ArrayList<>(10);

}
