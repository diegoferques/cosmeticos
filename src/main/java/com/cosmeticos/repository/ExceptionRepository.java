package com.cosmeticos.repository;

import com.cosmeticos.model.Exception;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Vinicius on 02/10/2017.
 */
public interface ExceptionRepository extends JpaRepository<Exception, Long> {

}
