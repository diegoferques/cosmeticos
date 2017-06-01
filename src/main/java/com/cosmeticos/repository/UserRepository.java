package com.cosmeticos.repository;

import com.cosmeticos.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by matto on 01/06/2017.
 */
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {
}
