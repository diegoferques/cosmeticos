package com.cosmeticos.commons;

import com.cosmeticos.model.Exception;
import lombok.Data;

import javax.validation.Valid;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Data
public class ExceptionRequestBody {
    @Valid
    private Exception entity;
}
