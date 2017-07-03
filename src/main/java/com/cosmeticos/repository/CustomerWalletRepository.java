package com.cosmeticos.repository;

import com.cosmeticos.model.CustomerWallet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Vinicius on 01/07/2017.
 */
@Transactional
public interface CustomerWalletRepository extends CrudRepository<CustomerWallet, Long> {
}
