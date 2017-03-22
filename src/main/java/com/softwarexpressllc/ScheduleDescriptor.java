package com.softwarexpressllc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScheduleDescriptor")
public class ScheduleDescriptor {
	String scheduleName;
	String scheduleDescription = "Default execution time: every 30 minutes";
	String executeAt = "0 0/30 * 1/1 * ? *";
		
	public ScheduleDescriptor () {
		
	}
	
	public ScheduleDescriptor(String scheduleName) {
		super();
		this.scheduleName = scheduleName;		
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getScheduleDescription() {
		return scheduleDescription;
	}

	public void setScheduleDescription(String scheduleDescription) {
		this.scheduleDescription = scheduleDescription;
	}

	public String getExecuteAt() {
		return executeAt;
	}

	public void setExecuteAt(String executeAt) {
		this.executeAt = executeAt;
	}

	@Override
	public String toString() {
		return "ScheduleDescriptor [scheduleName=" + scheduleName + ", scheduleDescription=" + scheduleDescription
				+ ", executeAt=" + executeAt + "]";
	}
}
