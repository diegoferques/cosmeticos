package com.cosmeticos.smtp;

import com.cosmeticos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

/**
 * Created by matto on 02/08/2017.
 */
//https://www.quickprogrammingtips.com/spring-boot/how-to-send-email-from-spring-boot-applications.html

public class MailSender {
    @Autowired
    private JavaMailSender sender;

    public Boolean sendPasswordReset(User user) {

        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(user.getEmail());
            helper.setText("Your new password is: " + user.getPassword());
            helper.setSubject("COSMÉTICOS - Your new password");

            sender.send(message);

            return true;

        }catch(Exception ex) {
            return false;
        }
    }

}
