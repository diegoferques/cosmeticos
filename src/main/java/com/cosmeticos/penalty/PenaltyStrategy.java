package com.cosmeticos.penalty;

import com.cosmeticos.model.User;

/**
 * Created by Lulu on 13/07/2017.
 */
public interface PenaltyStrategy {
    PenaltySummary execute(User user);
}
