package com.cosmeticos.repository;

import com.cosmeticos.model.Exception;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Vinicius on 02/10/2017.
 */
public interface ExceptionRepository extends JpaRepository<Exception, Long> {
    List<Exception> findByStatus(Exception.Status status);

}
