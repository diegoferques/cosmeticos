package com.cosmeticos.repository;

import com.cosmeticos.model.Customer;
import com.cosmeticos.model.Wallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by Vinicius on 01/07/2017.
 */
@Transactional
public interface CustomerWalletRepository extends CrudRepository<Wallet, Long> {
    void save(Customer customer);
}
