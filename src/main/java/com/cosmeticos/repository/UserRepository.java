package com.cosmeticos.repository;

import com.cosmeticos.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Vinicius on 29/05/2017.
 */
@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

}
