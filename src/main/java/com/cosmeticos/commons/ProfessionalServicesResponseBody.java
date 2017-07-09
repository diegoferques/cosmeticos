package com.cosmeticos.commons;

import com.cosmeticos.model.ProfessionalServices;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Data
public class ProfessionalServicesResponseBody {

    @JsonView(ResponseJsonView.ProfessionalServicesFindAll.class)
    private String description;

    @JsonView(ResponseJsonView.ProfessionalServicesFindAll.class)
    private List<ProfessionalServices> professionalServicesList = new ArrayList<>(10);

}
