package com.cosmeticos.repository;

import com.cosmeticos.model.Sale;
import com.cosmeticos.model.Sale;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by matto on 17/06/2017.
 */
public interface OrderRepository extends CrudRepository<Sale, Long> {
    List<Sale> findTop10ByOrderByDateDesc();

    List<Sale> findAll();
}
