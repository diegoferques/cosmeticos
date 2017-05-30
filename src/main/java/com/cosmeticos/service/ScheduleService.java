package com.cosmeticos.service;

import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Created by Lulu on 23/05/2017.
 */
@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository repository;

    public Schedule create(ScheduleRequestBody request)
    {
        Schedule s = new Schedule();
        s.setScheduleDate(request.getScheduleDate());
        s.setOwner(request.getOwnerUser());
        s.setStatus(Schedule.Status.ACTIVE);

        return repository.save(s);
    }

    public Schedule update(ScheduleRequestBody request)
    {
        Schedule schedule = repository.findOne(request.getIdSchedule());
        schedule.setScheduleDate(request.getScheduleDate());
        schedule.setOwner(request.getOwnerUser());
        schedule.setStatus(Schedule.Status.valueOf(request.getStatus()));
        return repository.save(schedule);
    }

    public Optional<Schedule> find(Long id)
    {
        return Optional.of(repository.findOne(id));
    }

    public void delete()
    {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Schedule> find10Lastest() {
        return repository.findTop10ByOrderByScheduleDateDesc();
    }
}
