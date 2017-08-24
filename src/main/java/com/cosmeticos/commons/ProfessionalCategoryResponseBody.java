package com.cosmeticos.commons;

import com.cosmeticos.model.ProfessionalCategory;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Data
public class ProfessionalCategoryResponseBody {

    @JsonView(ResponseJsonView.ProfessionalCategoryFindAll.class)
    private String description;

    @JsonView(ResponseJsonView.ProfessionalCategoryFindAll.class)
    private List<ProfessionalCategory> professionalCategoryList = new ArrayList<>(10);

}
