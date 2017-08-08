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


    List<Schedule> findTop10ByOrderByScheduleStartDesc();
}