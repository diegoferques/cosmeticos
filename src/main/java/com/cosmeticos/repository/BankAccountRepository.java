package com.cosmeticos.repository;

import com.cosmeticos.model.BankAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by matto on 01/06/2017.
 */
@Transactional
public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {

}
