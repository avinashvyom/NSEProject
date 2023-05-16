package com.vyomlabs.runidgenerationservice.service;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.vyomlabs.runidgenerationservice.dto.JobStatusResponse;

@Service
public interface RetryService {

	@Retryable(retryFor = { Exception.class }, maxAttempts = 4, backoff = @Backoff(delay = 4000))
	JobStatusResponse retryJobStatusResponseAPI(String alphaNumericId, String authHeader) throws Exception;

	@Retryable(retryFor = { Exception.class }, maxAttempts = 4, backoff = @Backoff(delay = 4000))
	String retryCaptureJobIdByRunJobsAPI(String authHeader, String folderId, String folderName) throws Exception;

}