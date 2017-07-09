package com.cosmeticos.repository;

import com.cosmeticos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Vinicius on 29/05/2017.
 */
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {

}
