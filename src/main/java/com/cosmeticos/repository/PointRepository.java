package com.cosmeticos.repository;

import com.cosmeticos.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByUserId(Long userId);

    /**
     * Retorna a soma de todos os pontos que o usuario possui.
     * @param userId
     * @return
     */
    @Query(
            "SELECT sum(value) as points FROM Point WHERE userId = ?1"
    )
    Long sumByUserId(Long userId);
}
