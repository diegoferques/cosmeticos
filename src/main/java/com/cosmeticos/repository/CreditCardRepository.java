package com.cosmeticos.repository;

import com.cosmeticos.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    List<CreditCard> findByUserEmail(String email);

}
