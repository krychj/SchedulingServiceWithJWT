package com.softwarexpressllc;

import java.util.List;

public interface MainService {	
	public List<ScheduleDescriptor> getSchedules();
	public String schedule(ScheduleDescriptor scheduleDesc);
}
