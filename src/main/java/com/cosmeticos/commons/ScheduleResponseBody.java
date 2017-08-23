package com.cosmeticos.commons;

import com.cosmeticos.model.Schedule;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lulu on 22/05/2017.
 */
@Data
public class ScheduleResponseBody {

	@JsonView({ 
		ResponseJsonView.ScheduleByProfessionalInRunningOrders.class 
	})
	private String description;

	@JsonView({ 
		ResponseJsonView.ScheduleByProfessionalInRunningOrders.class 
	})
	private List<Schedule> scheduleList = new ArrayList<>(10);

	public ScheduleResponseBody() {
	}

	public ScheduleResponseBody(Schedule schedule) {
		this.scheduleList.add(schedule);
	}
}
