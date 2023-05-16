package com.vyomlabs.runidgenerationservice.model;

import lombok.Data;

@Data
public class ActiveService {
	public String serviceName;
	public String status;
	public String statusReason;
	public String startTime;
	public String endTime;
	public String dueTime;
	public String slackTime;
	public String serviceOrderDateTime;
	public String scheduledOrderDate;
	public String serviceJob;
	public String serviceControlM;
	public String priority;
	public String note;
	public String totalJobs;
	public String jobsCompleted;
	public String jobsWithoutStatistics;
	public String completionPercentage;
	public String averageCompletionTime;
	public String errors;
	public StatusByJobs statusByJobs;

}
