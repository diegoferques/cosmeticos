package com.cosmeticos.repository;

import com.cosmeticos.model.Location;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by matto on 10/07/2017.
 */
public interface LocationRepository extends CrudRepository<Location, Long> {
    List<Location> findAll();

    List<Location> findTop10();
}
