package com.cosmeticos.commons;

import com.cosmeticos.model.Professional;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class ProfessionalResponseBody {

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    private String description;

    @JsonView({
            ResponseJsonView.ProfessionalFindAll.class,
            ResponseJsonView.ProfessionalUpdate.class,
            ResponseJsonView.ProfessionalCreate.class,
    })
    private List<Professional> professionalList = new ArrayList<>(10);

    public ProfessionalResponseBody() {
    }

    public ProfessionalResponseBody(Professional customer) {
        this.professionalList.add(customer);
    }
}
