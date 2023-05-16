package com.vyomlabs.runidgenerationservice.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vyomlabs.runidgenerationservice.dto.JobStatusResponse;
import com.vyomlabs.runidgenerationservice.model.Error;
import com.vyomlabs.runidgenerationservice.model.ErrorResponse;
import com.vyomlabs.runidgenerationservice.model.JobsStatus;
import com.vyomlabs.runidgenerationservice.model.RunJobsStatus;
import com.vyomlabs.runidgenerationservice.utility.HttpHeaderGenerator;
import com.vyomlabs.runidgenerationservice.utility.ResponseJsonBodyGenerator;
import com.vyomlabs.runidgenerationservice.utility.URLBuilder;

@Service
public class RetryServiceImpl implements RetryService {

	Logger logger = LoggerFactory.getLogger(RetryServiceImpl.class);

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private ResponseJsonBodyGenerator jsonBodyGenerator = new ResponseJsonBodyGenerator();

	private URLBuilder urlBuilder = new URLBuilder();

	@Autowired
	private RestTemplate restTemplate;

	static int retryJobStatusResponseAPICounter = 0;

	static int retryCaptureJobIdByRunJobsAPICounter = 0;


	@Override
	public JobStatusResponse retryJobStatusResponseAPI(String alphaNumericId, String authHeader) throws Exception {
		retryJobStatusResponseAPICounter++;
		if (retryJobStatusResponseAPICounter == 1) {
			logger.info("retryJobStatusResponseAPI() method accessed.....");
		} else {
			logger.info("Retrying retryJobStatusResponseAPI() method.....");
		}
		try {
			JobStatusResponse jobStatusResponse = new JobStatusResponse();
			StringBuffer sb = new StringBuffer();
			String JOB_STATUS_URL = sb.append(urlBuilder.buildJobStatusURL()).append(alphaNumericId).toString();
			logger.info("Job status url : " + JOB_STATUS_URL);
			HttpHeaders jobStatusHeaders = HttpHeaderGenerator.getHeaders();
			jobStatusHeaders.set("Authorization", authHeader);
			HttpEntity<String> jobStatusEntity = new HttpEntity<String>(jobStatusHeaders);
			jobStatusResponse = restTemplate
					.exchange(JOB_STATUS_URL, HttpMethod.GET, jobStatusEntity, JobStatusResponse.class).getBody();
			return jobStatusResponse;
		} catch (Exception e) {
			logger.info(e.getMessage());
			if (jsonBodyGenerator.getStatusCode(e.getMessage()) == 401
					|| jsonBodyGenerator.getStatusCode(e.getMessage()) == 500) {
				ErrorResponse errorResponse = new ErrorResponse();
				Error error = new Error();
				error.setMessage("User not found. Session token is invalid or expired.");
				ArrayList<Error> errorList = new ArrayList<>();
				errorList.add(error);
				errorResponse.setErrors(errorList);
				e.printStackTrace();
				logger.info(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(gson.toJson(errorResponse)).toString());
				throw e;
			}
			throw e;
		}
	}

	@Override
	public String retryCaptureJobIdByRunJobsAPI(String authHeader, String folderId, String folderName)
			throws Exception {
		retryCaptureJobIdByRunJobsAPICounter++;
		if (retryCaptureJobIdByRunJobsAPICounter == 1) {
			logger.info("retryCaptureJobIdByRunJobsAPI() method accessed.....");
		} else {
			logger.info("Retrying retryCaptureJobIdByRunJobsAPI() method.....");
		}
		try {
			String JobId = null;
			HttpHeaders runJobsHeaders = HttpHeaderGenerator.getHeaders();
			runJobsHeaders.set("Authorization", authHeader);
			HttpEntity<String> jobStatusEntity = new HttpEntity<String>(runJobsHeaders);
			logger.info("Required Http Entity for Run Jobs api: " + jobStatusEntity.toString());
			String RUN_JOBS_URL = urlBuilder.buildRunJobsURL(folderName);// + "?jobname=SLA_ControlM_"+folderName;
			logger.info("RUN_JOBS_URL is : " + RUN_JOBS_URL);
			JobsStatus jobsStatus = restTemplate
					.exchange(RUN_JOBS_URL, HttpMethod.GET, jobStatusEntity, JobsStatus.class).getBody();
			ArrayList<RunJobsStatus> statuses = jobsStatus.getStatuses();
			logger.info("Statuses from RUN_JOBS_URL : " + gson.toJson(statuses));
			Iterator<RunJobsStatus> statusesIterator = jobsStatus.getStatuses().iterator();
			while (statusesIterator.hasNext()) {
				RunJobsStatus runJobsStatus = statusesIterator.next();
				if (runJobsStatus.getFolderId().equals(folderId)) {
					JobId = runJobsStatus.getJobId();
					logger.info("Job Id is of SLA job is : " + JobId);
					break;
				}
			}
			return JobId;
		} catch (Exception e) {
			logger.error("Exception caught in retryCaptureJobIdByRunJobsAPI() method.. : " + e.getMessage());
			e.printStackTrace();
			if (jsonBodyGenerator.getStatusCode(e.getMessage()) == 401
					|| jsonBodyGenerator.getStatusCode(e.getMessage()) == 500) {
				ErrorResponse errorResponse = new ErrorResponse();
				Error error = new Error();
				error.setMessage("User not found. Session token is invalid or expired.");
				ArrayList<Error> errorList = new ArrayList<>();
				errorList.add(error);
				errorResponse.setErrors(errorList);
				e.printStackTrace();
				logger.info(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(gson.toJson(errorResponse)).toString());
				throw e;
			}
			logger.info(ResponseEntity.status(jsonBodyGenerator.getStatusCode(e.getMessage()))
					.body(jsonBodyGenerator.getJsonBody(e.getMessage())).toString());
			throw e;
		}
	}
}
