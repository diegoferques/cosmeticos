package com.cosmeticos.repository;

import com.cosmeticos.model.Schedule;
import com.cosmeticos.model.Service;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Vinicius on 31/05/2017.
 */
public interface ServiceRepository extends CrudRepository<Service, Long> {

}
