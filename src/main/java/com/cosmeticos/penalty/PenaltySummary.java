package com.cosmeticos.penalty;

import lombok.Data;
import lombok.Getter;

/**
 * Created by Lulu on 13/07/2017.
 */
@Data
public class PenaltySummary {

    public enum Status
    {
        OK, NOK;
    }

    public PenaltySummary(Status status, Class<? extends PenaltyStrategy> appliedStrategy) {
        this.status = status;
        this.appliedStrategy = appliedStrategy;
    }

    @Getter
    private Status status;

    @Getter
    private Class<? extends PenaltyStrategy> appliedStrategy;
}
