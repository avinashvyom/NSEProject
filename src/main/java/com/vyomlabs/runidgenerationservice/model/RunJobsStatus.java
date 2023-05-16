package com.vyomlabs.runidgenerationservice.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class RunJobsStatus {
	public String jobId;
	public String folderId;
	public int numberOfRuns;
	public String name;
	public String folder;
	public String type;
	public String status;
	public boolean held;
	public boolean deleted;
	public boolean cyclic;
	public String startTime;
	public String endTime;
	public ArrayList<String> estimatedStartTime;
	public ArrayList<String> estimatedEndTime;
	public String orderDate;
	public String ctm;
	public String description;
	public String host;
	public String library;
	public String application;
	public String subApplication;
	public String jobJSON;
	public String outputURI;
	public String logURI;

}
