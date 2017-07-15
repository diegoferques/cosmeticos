package com.cosmeticos.penalty;

import com.cosmeticos.model.User;
import org.springframework.stereotype.Service;

/**
 * Created by Lulu on 13/07/2017.
 */
@Service(PenaltyType.BASIC_USER_ABORTING)
public class BasicUserAbortingPenaltyStrategy implements PenaltyStrategy {
    @Override
    public PenaltySummary execute(User user) {
        throw new UnsupportedOperationException("Estrategia nao implementada: " + getClass().getSimpleName());
    }
}
