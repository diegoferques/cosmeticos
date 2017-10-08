package com.cosmeticos.service;

import com.cosmeticos.commons.ExceptionRequestBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.commons.RoleRequestBody;
import com.cosmeticos.model.Exception;
import com.cosmeticos.model.Role;
import com.cosmeticos.repository.ExceptionRepository;
import com.cosmeticos.smtp.MailSenderService;
import com.cosmeticos.validation.ExceptionEntityValidationException;
import com.cosmeticos.validation.UserValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Service
public class ExceptionService {

    @Autowired
    private MailSenderService mailSenderService;

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

    public Optional<Exception> update(ExceptionRequestBody request) {
        Exception exceptionFromRequest = request.getEntity();

        // TODO ver possibilidade de usar VO pq para update, o ID deve ser obrigatorio.
        Long requestedIdException = exceptionFromRequest.getId();

        Optional<Exception> optional = Optional.ofNullable(exceptionRepository.findOne(requestedIdException));

        if (optional.isPresent()) {
            Exception persistentException = optional.get();

            persistentException.setStackTrace(exceptionFromRequest.getStackTrace());
            persistentException.setStatus(exceptionFromRequest.getStatus());

            exceptionRepository.save(persistentException);
        }

        return optional;
    }

    public Exception sendEmailWithException(Exception entity){

        Optional<Exception> exceptionOptional = exceptionRepository.findByStatus(Exception.Status.UNRESOLVED);

        if(exceptionOptional.isPresent()){

            Exception persistentException = exceptionOptional.get();

            String stackTrace = persistentException.getStackTrace().substring(0, 254);

            Exception.Status status = persistentException.getStatus();

            Boolean sendEmail = mailSenderService.sendEmail(entity.getEmail(),
                    "Exceção lançada",
                    stackTrace);

            if(sendEmail == true){
                persistentException.setStackTrace(stackTrace);
                persistentException.setStatus(status);

                return persistentException;
            }else{
                throw new ExceptionEntityValidationException(ResponseCode.EXCEPTION_SEND_EMAIL_FAIL, "Falha ao enviar Exceção.");
            }
        }else {
            return null;
        }

    }
}
