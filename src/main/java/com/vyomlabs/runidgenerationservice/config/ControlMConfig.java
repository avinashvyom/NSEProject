package com.vyomlabs.runidgenerationservice.config;

import lombok.Data;

@Data
public class ControlMConfig {

	public String sessionLoginApi;

	public String orderApi;

	public String jobStatusApi;

	public String SLAStatusApi;

	public String RunJobsApi;

	public String serverName;

	public int port;

}
