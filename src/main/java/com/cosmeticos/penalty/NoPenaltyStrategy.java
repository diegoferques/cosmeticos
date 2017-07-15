package com.cosmeticos.penalty;

import com.cosmeticos.model.User;
import org.springframework.stereotype.Service;

/**
 * Created by Lulu on 13/07/2017.
 */
@Service(PenaltyType.NONE)
public class NoPenaltyStrategy implements PenaltyStrategy {
    @Override
    public PenaltySummary execute(User user) {

        PenaltySummary summary = new PenaltySummary(
                PenaltySummary.Status.OK,
                getClass()
        );

        return summary;
    }
}
