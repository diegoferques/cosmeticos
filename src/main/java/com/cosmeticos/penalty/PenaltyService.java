package com.cosmeticos.penalty;

import com.cosmeticos.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by Lulu on 13/07/2017.
 */
@Service
public class PenaltyService {

    @Autowired
    private ApplicationContext applicationContext;

    public PenaltySummary apply(User user, PenaltyType.Value penaltyType)
    {
        PenaltyStrategy penaltyStrategy =
                (PenaltyStrategy) applicationContext.getBean(penaltyType.getValue());

        return penaltyStrategy.execute(user);
    }
}
