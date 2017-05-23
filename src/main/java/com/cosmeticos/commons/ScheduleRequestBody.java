package com.cosmeticos.commons;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Lulu on 22/05/2017.
 */
@Data
public class ScheduleRequestBody {

    @NotEmpty(message = "ownerUser email was not set!")
    private String ownerUser;

    @NotNull(message = "ScheduleDate cannot be null!")
    private Date scheduleDate;
}
