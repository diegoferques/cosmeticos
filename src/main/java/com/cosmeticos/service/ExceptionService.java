package com.cosmeticos.service;

import com.cosmeticos.commons.ExceptionRequestBody;
import com.cosmeticos.commons.ResponseCode;
import com.cosmeticos.model.Exception;
import com.cosmeticos.model.Order;
import com.cosmeticos.repository.ExceptionRepository;
import com.cosmeticos.smtp.MailSenderService;
import com.cosmeticos.validation.ExceptionEntityValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Slf4j
@Service
public class ExceptionService {

    @Value("${spring.mail.usernametest}")
    private String email;

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

        return exceptionRepository.save(exception);
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

    @Scheduled(cron = "${exception.unresolved.cron}")
    public void sendEmailWithException(){

        List<Exception> exceptionList = exceptionRepository.findByStatus(Exception.Status.UNRESOLVED);

        for(Exception e : exceptionList ) {
            if (!exceptionList.isEmpty()) {


                String stackTrace = e.getStackTrace();
                String deviceModel = e.getDeviceModel();
                String osVersion = e.getOsVersion();
                String email = e.getEmail();
                Exception.Status status = e.getStatus();

                Boolean sendEmail = mailSenderService.sendEmail(this.email,
                        "Exceção lançada",
                        stackTrace + ", " + osVersion + ", " + deviceModel + ", " + email + ", " + status);

                if (sendEmail == true) {
                    log.info("" + stackTrace + "" + osVersion + "" + deviceModel + "" + email + "" + status);
                }
            }
        }
    }

}
