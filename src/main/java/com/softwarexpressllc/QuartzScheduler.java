package com.softwarexpressllc;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuartzScheduler {
	Scheduler scheduler;
	
	@PostConstruct
	void init() {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();			
			scheduler.start();			
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	void destroy() {
		try {			
			scheduler.shutdown(true);
			System.out.println("Terminatied Quartz Scheduler");
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
}
