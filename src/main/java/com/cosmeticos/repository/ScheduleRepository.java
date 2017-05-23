package com.cosmeticos.repository;

import com.cosmeticos.model.Schedule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Responsavel por todas as operacoes de insercao, leitura e atualizacao de dados no banco de dados.
 * Created by Lulu on 22/05/2017.
 */
@Transactional
public interface ScheduleRepository extends CrudRepository<Schedule, Long> {
    public Schedule findByScheduleId(Long id);
    public Schedule findByScheduleDate(String url);

    public List<Schedule> findAllByOrderByScheduleId();
    public List<Schedule> findAllByOrderByScheduleIdDesc();
    public List<Schedule> findAllByOrderByScheduleDate();
    public List<Schedule> findAllByOrderByScheduleDateDesc();

}