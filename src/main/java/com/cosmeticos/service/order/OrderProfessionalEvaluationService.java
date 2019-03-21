package com.cosmeticos.service.order;

import com.cosmeticos.model.*;
import com.cosmeticos.service.VoteService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Aplicado ao Professional quando o customer confirma realização do servico e avalia o professional
 */
@Service(OrderStatus.Values.READY2CHARGE_NAME)
public class OrderProfessionalEvaluationService implements OrderStatusHandler {

    @Autowired
    private VoteService voteService;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {

        ProfessionalCategory persistentProfessionalCategory = persistentOrder.getProfessionalCategory();
        User persistentUser = persistentProfessionalCategory.getProfessional().getUser();

        ProfessionalCategory receivedProfessionalCategory = transientOrder.getProfessionalCategory();
        User receivedUser = receivedProfessionalCategory.getProfessional().getUser();

        if (!receivedUser.getVoteCollection().isEmpty()) {
            Vote receivedvote = receivedUser.getVoteCollection().stream().findFirst().get();

            addVotesToUser(persistentUser, receivedvote);

            MDC.put("professionalVote", String.valueOf(receivedvote.getValue()));
        }
    }

    private void addVotesToUser(User persistentUser, Vote receivedVote) {

        persistentUser.addVote(receivedVote);
        voteService.create(receivedVote);
        persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));
    }
}
