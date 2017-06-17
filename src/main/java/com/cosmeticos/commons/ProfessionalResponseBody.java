package com.cosmeticos.commons;

import com.cosmeticos.model.Professional;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class ProfessionalResponseBody {

    private String description;

    private List<Professional> professionalList = new ArrayList<>(10);

    public ProfessionalResponseBody() {
    }

    public ProfessionalResponseBody(Professional customer) {
        this.professionalList.add(customer);
    }
}
