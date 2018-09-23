package com.cosmeticos.repository;

import com.cosmeticos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


/**
 * Created by Vinicius on 29/05/2017.
 */
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}
