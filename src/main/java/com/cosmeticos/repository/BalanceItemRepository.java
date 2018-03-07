package com.cosmeticos.repository;

import com.cosmeticos.model.BalanceItem;
import com.cosmeticos.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BalanceItemRepository extends JpaRepository<BalanceItem, Long> {

}
