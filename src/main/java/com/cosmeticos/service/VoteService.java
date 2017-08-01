package com.cosmeticos.service;

import com.cosmeticos.commons.OrderRequestBody;
import com.cosmeticos.model.Professional;
import com.cosmeticos.model.User;
import com.cosmeticos.model.Vote;
import com.cosmeticos.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by matto on 01/08/2017.
 */
@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;

    public void create(OrderRequestBody orderRequestBody) {
        Vote vote = new Vote();
        vote.setUser(orderRequestBody.getOrder().getProfessionalServices().getProfessional().getUser());
        vote.setValue(orderRequestBody.getVote());
        voteRepository.save(vote);
    }

    public Optional<Vote> find(Long id){
        return Optional.ofNullable(voteRepository.findOne(id));
    }

    public List<Vote> findAllByUser(User user){
        return voteRepository.findAllByUser(user);
    }

    public float getProfessionalEvaluation(Professional professional) {
        float evaluation = 0;
        Integer totalVotes = 0;
        List<Vote> votes = findAllByUser(professional.getUser());

        for (Vote vote: votes) {
            totalVotes += vote.getValue();
        }

        evaluation = totalVotes / votes.size();

        return evaluation;
    }


}
