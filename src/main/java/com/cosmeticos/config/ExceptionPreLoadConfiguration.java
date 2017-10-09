package com.cosmeticos.config;


import com.cosmeticos.model.Exception;
import com.cosmeticos.repository.ExceptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Calendar;

import static java.time.LocalDateTime.now;

@Slf4j
@Component
@Profile("default")
public class ExceptionPreLoadConfiguration {

    @Autowired
    private ExceptionRepository exceptionRepository;

    @PostConstruct
    public void insertInitialH2Data() throws ParseException {

        Exception exception1 = new Exception();
        exception1.setStackTrace("testStackTrace");
        exception1.setDeviceModel("xing-ling");
        exception1.setOsVersion("android nougat");
        exception1.setDate(now());
        exception1.setStatus(Exception.Status.RESOLVED);

        Exception exception2 = new Exception();
        exception2.setStackTrace(fakeStackTrace().substring(0, 244));
        exception2.setDeviceModel("xing-ling");
        exception2.setOsVersion("android lollypop");
        exception2.setDate(now().plusDays(-2));
        exception2.setStatus(Exception.Status.UNRESOLVED);

        Exception exception3 = new Exception();
        exception3.setStackTrace(fakeStackTrace().substring(0, 244));
        exception3.setDeviceModel("xing-ling");
        exception3.setOsVersion("android kit kat");
        exception3.setDate(now());
        exception3.setStatus(Exception.Status.UNRESOLVED);

        exceptionRepository.save(exception1);
        exceptionRepository.save(exception2);
        exceptionRepository.save(exception3);
    }

    private String fakeStackTrace()
    {
        try{
            try{
                throw new IllegalStateException("dei erro");
            } catch(java.lang.Exception e){
                throw new RuntimeException("dei erro de novo");
            }
        }catch(java.lang.Exception e){

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String sStackTrace = sw.toString();

            return sStackTrace;
        }
    }
}
