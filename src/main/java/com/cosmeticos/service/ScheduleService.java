package com.cosmeticos.service;

import com.cosmeticos.commons.ScheduleRequestBody;
import com.cosmeticos.model.Schedule;
import com.cosmeticos.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * Created by Lulu on 23/05/2017.
 */
@org.springframework.stereotype.Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository repository;

    public Schedule update(ScheduleRequestBody request)
    {
        Schedule schedule = repository.findById(request.getIdSchedule()).get();
        schedule.setScheduleStart(request.getScheduleStart());
        return repository.save(schedule);
    }

    public Optional<Schedule> find(Long id)
    {
        return (repository.findById(id));
    }

    public void delete()
    {
        throw new UnsupportedOperationException("Nao deletaremos registros, o status dele definirá sua situação.");
    }

    public List<Schedule> find10Lastest() {
        return repository.findTop10ByOrderByScheduleStartDesc();
    }

    /**
     * Retorna agendamentos associados a orders iniciadas de um profissional.
     * @param idProfessional
     * @return
     */
	public List<Schedule> findByProfessional(Long idProfessional) {
		return repository.findByProfessional(idProfessional);
	}
}
