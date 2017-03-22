package com.softwarexpressllc;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/api/main")
public class MainServiceImpl implements MainService {
	
	@Context
	HttpServletRequest currentRequest;
		
	@SuppressWarnings("unchecked")
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/schedules")
	public List<ScheduleDescriptor> getSchedules() {				
		HttpSession session = currentRequest.getSession();
		List<ScheduleDescriptor> schedules = (List<ScheduleDescriptor>) session.getAttribute("schedules");		
		return schedules;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/schedule")
	public String schedule(ScheduleDescriptor scheduleDesc) {		
		if(scheduleDesc != null) {
			HttpSession session = currentRequest.getSession();
			List<ScheduleDescriptor> schedules = (List<ScheduleDescriptor>) session.getAttribute("schedules");
			if(schedules == null) {									
				schedules = new ArrayList<>();				
				session.setAttribute("schedules", schedules);
			}
			schedules.add(scheduleDesc);
			return "Schedule: " + "<" + scheduleDesc.getScheduleName() + "> was accepted.";
		} else {
			return "No schedule recieved";
		}	
	}
}
