package com.cosmeticos.config;

import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Classe que so vai executar em dev, pois o profile de producao sera PRODUCTION.
 * Created by Lulu on 26/05/2017.
 */
@Configuration
@Profile("default")
public class SchedulePreLoadConfiguration {

    @Autowired
    private ScheduleRepository repository;

    @PostConstruct
    public void insertInitialH2Data()
    {

        for (int i = 0; i < 100; i++) {

            Schedule s = new Schedule();
            s.setScheduleDate(Timestamp.valueOf(LocalDateTime.of(2017
                    , (int) (System.nanoTime() % 11) + 1
                    , (int) (System.nanoTime() % 28) + 1
                    , (int) (System.nanoTime() % 24)
                    , 0)));

            Schedule.Status[] allStatus = Schedule.Status.values();
            int randomStatusIndex = (int)(System.nanoTime() % allStatus.length);

            s.setStatus(allStatus[randomStatusIndex]);

            repository.save(s);
        }

    }
}
