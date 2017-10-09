package com.cosmeticos.config;


import com.cosmeticos.model.Exception;
import com.cosmeticos.repository.ExceptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;

@Slf4j
@Component
@Profile("default")
public class ExceptionPreLoadConfiguration {

    @Autowired
    private ExceptionRepository exceptionRepository;

    @PostConstruct
    public void insertInitialH2Data() throws ParseException {

        Exception exception = new Exception();
        exception.setStackTrace("testStackTrace");
        exception.setEmail("killerhomage@gmail.com");
        exception.setDeviceModel("xing-ling");
        exception.setOsVersion("android");

        exceptionRepository.save(exception);
    }
}
