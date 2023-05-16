package com.vyomlabs.runidgenerationservice.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class JobsStatus {
	public String completion;
	public ArrayList<RunJobsStatus> statuses;
	public int startIndex;
	public int itemsPerPage;
	public int returned;
	public int total;
	public String sort;
	public String nextURI;
	public String prevURI;
	public String monitorPageURI;

}
