package com.vyomlabs.runidgenerationservice.model;

import lombok.Data;

@Data
public class StatusByJobs {
	public String executed;
	public String waitCondition;
	public String waitResource;
	public String waitUser;
	public String waitHost;
	public String waitWorkload;
	public String completed;
	public String error;
	public String unknown;

}
