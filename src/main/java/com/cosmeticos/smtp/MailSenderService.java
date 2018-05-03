package com.cosmeticos.smtp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;


/**
 * Created by matto on 02/08/2017.
 */
//https://www.quickprogrammingtips.com/spring-boot/how-to-send-email-from-spring-boot-applications.html
@Slf4j
@Component
public class MailSenderService {

    @Autowired
    MailSender mailSender;

    @Value("${spring.mail.username}")
    private String webmasterEmail;

    //TODO - ADICIONAR TEMPLATE HTML PARA ENVIAR UM EMAIL BONITINHO PRO CLIENTE
    public Boolean sendEmail(String email, String subject, String msg) {


        SimpleMailMessage message = new SimpleMailMessage();

        message.setSubject(subject);
        message.setText(msg);
        message.setTo(email);
        message.setFrom(webmasterEmail);

        try {
            mailSender.send(message);

            return true;

        } catch (Exception e) {
            log.error("Erro ao enviar o email: " + e.getMessage(), e);
            return false;
        }

    }

}
