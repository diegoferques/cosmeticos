package com.cosmeticos.repository;

import com.cosmeticos.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
