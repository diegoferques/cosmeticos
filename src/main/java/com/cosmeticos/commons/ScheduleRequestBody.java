package com.cosmeticos.commons;

import com.cosmeticos.model.Schedule;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by Lulu on 22/05/2017.
 */
@Data
public class ScheduleRequestBody {

    private Long idSchedule;

    @NotEmpty(message = "ownerUser email was not set!")
    private String ownerUser;

    @NotNull(message = "ScheduleDate cannot be null!")
    private Date scheduleDate;

    private String status;


}
