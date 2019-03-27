package com.cosmeticos.repository;

import com.cosmeticos.model.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByUserId(Long userId);
}
