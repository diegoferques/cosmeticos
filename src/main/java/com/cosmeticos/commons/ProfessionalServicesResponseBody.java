package com.cosmeticos.commons;

import com.cosmeticos.model.ProfessionalServices;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Data
public class ProfessionalServicesResponseBody {

    private String description;

    private List<ProfessionalServices> professionalServicesList = new ArrayList<>(10);
}
