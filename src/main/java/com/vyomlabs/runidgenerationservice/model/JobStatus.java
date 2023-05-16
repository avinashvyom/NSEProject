package com.vyomlabs.runidgenerationservice.model;

import lombok.Data;

@Data
public class JobStatus {

	private String alphanumericRunId;
	private String statusURI;
	private Integer numericRunId;

}
