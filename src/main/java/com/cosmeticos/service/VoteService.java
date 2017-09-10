package com.cosmeticos.service;

import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.model.Vote;
import com.cosmeticos.repository.OrderRepository;
import com.cosmeticos.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 01/08/2017..
 */
@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private OrderRepository orderRepository;

    public void create(Vote vote) {
        voteRepository.save(vote);
    }

    public Optional<Vote> find(Long id){
        return Optional.ofNullable(voteRepository.findOne(id));
    }

    public List<Vote> findAllByUser(User user){
        return voteRepository.findAllByUser(user);
    }

    public float getUserEvaluation(User user) {

        float evaluation = 0;
        Integer totalVotes = 0;

        List<Vote> votes = findAllByUser(user);

        if (votes.size() > 0) {
            for (Vote vote: votes) {
                int voteValue = vote.getValue() == null ? 0 : vote.getValue();
                totalVotes += voteValue;
            }
            evaluation = totalVotes / votes.size();
            return evaluation;
        }
        else
        {
            return 0.0f;
        }
    }




}
