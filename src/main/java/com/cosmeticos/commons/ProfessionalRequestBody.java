package com.cosmeticos.commons;

import com.cosmeticos.model.Professional;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by matto on 27/05/2017.
 */
@Data
public class ProfessionalRequestBody {
/*
    @JsonView(
            {
                    ResponseJsonView.ProfessionalCreate.class,
            })
            */
    @Valid
    private Professional professional;
}
