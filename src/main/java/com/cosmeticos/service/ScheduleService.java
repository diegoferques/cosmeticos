package com.cosmeticos.service;

import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Lulu on 23/05/2017.
 */
@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository repository;

    public Schedule create(ScheduleRequestBody request)
    {
        LocalDateTime ldt = LocalDateTime.ofInstant(request.getScheduleDate().toInstant(), ZoneId.systemDefault());

        Schedule s = new Schedule();
        s.setScheduleDate(ldt);
        s.setOwner(request.getOwnerUser());
        s.setStatus(Schedule.Status.ACTIVE);

        return repository.save(s);
    }

    public Schedule update(ScheduleRequestBody request)
    {
        LocalDateTime ldt = LocalDateTime.ofInstant(request.getScheduleDate().toInstant(), ZoneId.systemDefault());

        Schedule schedule = repository.findOne(request.getIdSchedule());
        schedule.setScheduleDate(ldt);
        schedule.setOwner(request.getOwnerUser());
        schedule.setStatus(Schedule.Status.valueOf(request.getStatus()));
        return repository.save(schedule);
    }

    public Schedule find(Long id)
    {
        return repository.findOne(id);
    }

    public void delete()
    {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }
}
