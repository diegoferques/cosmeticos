package com.cosmeticos.commons;

import com.cosmeticos.model.Category;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Data
public class CategoryRequestBody {

    @Valid
    private Category entity;
}
