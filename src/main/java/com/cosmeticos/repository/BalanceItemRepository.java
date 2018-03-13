package com.cosmeticos.repository;

import com.cosmeticos.model.BalanceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceItemRepository extends JpaRepository<BalanceItem, Long> {

    List<BalanceItem> findByEmail(String email);
}
