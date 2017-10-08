package com.cosmeticos.commons;

import com.cosmeticos.model.Exception;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Data
public class ExceptionResponseBody {

    private String description;

    private List<Exception> exceptionList = new ArrayList<>(10);

    public ExceptionResponseBody() {
    }

    public ExceptionResponseBody(Exception exception) {
        this.exceptionList.add(exception);
    }
}
