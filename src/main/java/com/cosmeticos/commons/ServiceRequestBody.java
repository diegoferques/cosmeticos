package com.cosmeticos.commons;

import com.cosmeticos.model.Service;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Vinicius on 31/05/2017.
 */
@Data
public class ServiceRequestBody {

    @Valid
    private Service entity;
}
