package com.cosmeticos.service;

import com.cosmeticos.commons.ExceptionRequestBody;
import com.cosmeticos.model.Exception;
import com.cosmeticos.repository.ExceptionRepository;
import com.cosmeticos.smtp.MailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

/**
 * Created by Vinicius on 02/10/2017.
 */
@Slf4j
@Service
public class ExceptionService {

    @Value("${exception.unresolved.destination.email}")
    private String email;

    @Value("${exception.unresolved.destination.subject}")
    private String subject;

    @Value("${exception.unresolved.destination.messagePattern}")
    private String emailMessagePattern;

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

        if (!exceptionList.isEmpty()) {
            for(Exception e : exceptionList ) {

                String stackTrace = e.getStackTrace();
                String deviceModel = e.getDeviceModel();
                String osVersion = e.getOsVersion();
                Exception.Status status = e.getStatus();

                // Sistema Operacional: {0}, Modelo do Aparelho: {1}, Status da Falha: {2}\n\nStack Trace:\n\n{3}
                String message = MessageFormat.format(
                        this.emailMessagePattern,
                        osVersion,
                        deviceModel,
                        status,
                        stackTrace);

                Boolean sendEmail = mailSenderService.sendEmail(email, subject, message);

                if (sendEmail) {
                    log.info("Enviado alerta de stacktrace para {}. ExceptionId: {}", email, e.getId());
                }
                else
                {
                    log.error("Falha enviando alerta de stacktrace para {}. ExceptionId: {}", email, e.getId());
                }
            }
        }
        else
        {
            log.debug("Nenhuma exception UNRESOLVED registrada.");
        }
    }

}
