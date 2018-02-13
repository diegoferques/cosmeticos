package com.cosmeticos.repository;

import com.cosmeticos.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by matto on 01/06/2017.
 */
@Transactional
public interface ImageRepository extends JpaRepository<Image, Long> {

}
