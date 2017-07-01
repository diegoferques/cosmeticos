package com.cosmeticos.repository;

import com.cosmeticos.model.CreditCard;
import com.cosmeticos.model.Hability;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by matto on 22/05/2017.
 */
@Transactional
public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {
}
