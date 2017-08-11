package com.cosmeticos.commons;

import lombok.Data;

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
    private Long idCategory;

    @NotNull(message = "ScheduleStart cannot be null!")
    private Date scheduleStart;

    @NotNull(message = "ScheduleEnd cannot be null")
    private Date scheduleEnd;

}
