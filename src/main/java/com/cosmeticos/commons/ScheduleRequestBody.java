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

    @NotNull(message = "Customer ID was not set!")
    private Long idCustomer;

    @NotNull(message = "Professional ID was not set!")
    private Long idProfessional;

    @NotNull(message = "Service ID was not set!")
    private Long idService;

    @NotNull(message = "ScheduleDate cannot be null!")
    private Date scheduleDate;

    private String status;
}
