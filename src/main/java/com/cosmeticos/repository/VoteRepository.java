package com.cosmeticos.repository;

import com.cosmeticos.model.User;
import com.cosmeticos.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by matto on 01/08/2017.
 */
@Transactional
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAllByUser(User user);


}
