package com.cosmeticos.commons;

import com.cosmeticos.model.ProfessionalCategory;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Vinicius on 21/06/2017.
 */
@Data
public class ProfessionalCategoryRequestBody {
    @Valid
    private ProfessionalCategory entity;

}
