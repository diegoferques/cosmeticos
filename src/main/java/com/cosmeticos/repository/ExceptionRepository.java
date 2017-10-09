package com.cosmeticos.repository;

import com.cosmeticos.model.Exception;
import com.cosmeticos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by Vinicius on 02/10/2017.
 */
public interface ExceptionRepository extends JpaRepository<Exception, Long> {
    List<Exception> findByStatus(Exception.Status status);

}
