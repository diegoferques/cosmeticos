package com.cosmeticos.service.order;

import com.cosmeticos.model.*;
import com.cosmeticos.service.VoteService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Aplicado ao Customer quando o professional encerra o servico
 */
@Service(OrderStatus.Values.SEMI_CLOSED_NAME)
public class OrderCustomerEvaluationService implements OrderStatusHandler {

    @Autowired
    private VoteService voteService;

    @Override
    public void handle(Order transientOrder, Order persistentOrder) {

        User persistentUser = persistentOrder.getIdCustomer().getUser();

        Customer receivedCustomer = transientOrder.getIdCustomer();

        if (receivedCustomer != null) {
            User receivedUser = transientOrder.getIdCustomer().getUser();

            if (receivedUser != null) {
                Set<Vote> voteCollection = receivedUser.getVoteCollection();
                if (voteCollection != null && !voteCollection.isEmpty()) {
                    Vote receivedvote = voteCollection.stream().findFirst().get();

                    if (receivedvote != null) {
                        addVotesToUser(persistentUser, receivedvote);
                        MDC.put("customerVote", String.valueOf(receivedvote.getValue()));
                    }
                }
            }
        }
    }

    private void addVotesToUser(User persistentUser, Vote receivedVote) {
        persistentUser.addVote(receivedVote);
        voteService.create(receivedVote);
        persistentUser.setEvaluation(voteService.getUserEvaluation(persistentUser));
    }
}
