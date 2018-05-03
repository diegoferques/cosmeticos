package com.cosmeticos.repository;

import com.cosmeticos.model.PriceRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Transactional
public interface PriceRuleRepository extends JpaRepository<PriceRule, Long> {

}
