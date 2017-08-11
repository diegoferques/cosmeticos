package com.cosmeticos.smtp;

import com.cosmeticos.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;


/**
 * Created by matto on 02/08/2017.
 */
//https://www.quickprogrammingtips.com/spring-boot/how-to-send-email-from-spring-boot-applications.html
@Slf4j
@Controller
public class MailSenderService {

    @Autowired
    MailSender mailSender;

    //TODO - ADICIONAR TEMPLATE HTML PARA ENVIAR UM EMAIL BONITINHO PRO CLIENTE
    public Boolean sendPasswordReset(User user) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("COSMÉTICOS - Your new password");
        message.setText("Your new password is: " + user.getPassword());
        message.setTo(user.getEmail());
        message.setFrom("projetoubeleza@gmail.com");

        try {
            mailSender.send(message);
            log.error("Email enviado com sucesso:");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Erro ao enviar o email: ", e.getMessage());
            return false;
        }

    }

}
