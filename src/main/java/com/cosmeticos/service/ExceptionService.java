package com.cosmeticos.service;

import com.cosmeticos.commons.ExceptionRequestBody;
import com.cosmeticos.model.Exception;
import com.cosmeticos.repository.ExceptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Service
public class ExceptionService {

    @Autowired
    private ExceptionRepository exceptionRepository;

    public Exception create(ExceptionRequestBody requestBody){
        Exception exception =requestBody.getEntity();

        if(exception.getStackTrace().length() > 255 ){

            String nova = exception.getStackTrace().substring(0, 254);

            exception.setStackTrace(nova);
        }
        return exceptionRepository.save(requestBody.getEntity());
    }
}
