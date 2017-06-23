package com.softwarexpressllc;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;

@Path("/api/main")
public class MainServiceImpl implements MainService {
	
	@Context
	HttpServletRequest currentRequest;
		
	@Override
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/test")
	public ScheduleDescriptor getTestSchedule() {				
		ScheduleDescriptor scheduleDesc = new ScheduleDescriptor("Test");				
		return scheduleDesc;
	}
	
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
	@Secured ({Role.ROLE_1})
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
			scheduleJob(scheduleDesc);
			return "Schedule: " + "<" + scheduleDesc.getScheduleName() + "> was accepted.";
		} else {
			return "No schedule received";
		}	
	}
	
	@Inject
	QuartzScheduler quartzScheduler;
	
	void scheduleJob(ScheduleDescriptor scheduleDesc) {
		try {
			String scheduleName = scheduleDesc.getScheduleName();
			System.out.println("Excecuting schedule: " + scheduleName);
			Scheduler scheduler = quartzScheduler.getScheduler();
			JobKey jobKeyTriggerSchedule = new JobKey("Trigger schedule", "SchedulingServiceGroup");
			JobDetail jobTriggerSchedule = JobBuilder.newJob(ScheduleExecutorQuartzJob.class)
					.withIdentity(jobKeyTriggerSchedule).build();
			SimpleTrigger triggerOnce = (SimpleTrigger) TriggerBuilder.newTrigger()
					.withIdentity("OnlyOnceTrigger", "EventsGroup").forJob(jobTriggerSchedule).startNow().build();			
			scheduler.scheduleJob(jobTriggerSchedule, triggerOnce);
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
        
            
	}
}
