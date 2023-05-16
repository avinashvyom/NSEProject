package com.vyomlabs.runidgenerationservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.vyomlabs.runidgenerationservice.config.ModelMapperConfig;
import com.vyomlabs.runidgenerationservice.dto.JobStatusResponse;
import com.vyomlabs.runidgenerationservice.dto.OrderDTO;
import com.vyomlabs.runidgenerationservice.dto.Status;
import com.vyomlabs.runidgenerationservice.model.ActiveService;
import com.vyomlabs.runidgenerationservice.model.Error;
import com.vyomlabs.runidgenerationservice.model.ErrorResponse;
import com.vyomlabs.runidgenerationservice.model.JobStatus;
import com.vyomlabs.runidgenerationservice.model.LoginRequest;
import com.vyomlabs.runidgenerationservice.model.Order;
import com.vyomlabs.runidgenerationservice.model.OrderResponse;
import com.vyomlabs.runidgenerationservice.model.SLAServices;
import com.vyomlabs.runidgenerationservice.utility.HttpHeaderGenerator;
import com.vyomlabs.runidgenerationservice.utility.RandomRunIdGenerator;
import com.vyomlabs.runidgenerationservice.utility.ResponseJsonBodyGenerator;
import com.vyomlabs.runidgenerationservice.utility.RunIdPersister;
import com.vyomlabs.runidgenerationservice.utility.URLBuilder;

/**
 * @author Prasad Dharmadhikari
 *
 */
/**
 * @author Prasad Dharmadhikari
 *
 */
@Service
public class OrderService {

	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RetryService retryService;

	private JobStatus jobStatus = new JobStatus();

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private RunIdPersister runIdPersister = new RunIdPersister();

	private ModelMapperConfig mapperConfig = new ModelMapperConfig();

	private ModelMapper modelMapper = mapperConfig.getModelMapper();

	private URLBuilder urlBuilder = new URLBuilder();

	private ResponseJsonBodyGenerator jsonBodyGenerator = new ResponseJsonBodyGenerator();

	public OrderService() {
		super();
	}

	/**
	 * @param loginRequest
	 * @return Login response from ControlM with username and password
	 * @throws JsonSyntaxException
	 * @throws JsonProcessingException
	 */
	@SuppressWarnings({ "deprecation", "null", "static-access" })
	public ResponseEntity<Object> authenticateUser(LoginRequest loginRequest)
			throws JsonSyntaxException, JsonProcessingException {
		logger.trace("authenticateUser method accessed...");
		ResponseEntity<Object> authenticationResponse = null;
		try {
			logger.trace("Inside try block...");
			String loginDetails = gson.toJson(loginRequest);
			HttpHeaders authenticationHeaders = HttpHeaderGenerator.getHeaders();
			HttpEntity<String> authenticationEntity = new HttpEntity<String>(loginDetails, authenticationHeaders);
			logger.info("authenticationEntity : " + authenticationEntity.toString());
			String LOGIN_URL = urlBuilder.buildLoginURL();
			logger.info("Login URL: " + LOGIN_URL);
			authenticationResponse = restTemplate.exchange(LOGIN_URL, HttpMethod.POST, authenticationEntity,
					Object.class);
			logger.info("Authentication Response Body : " + authenticationResponse.getBody());
			logger.info("Authentication Response Body code : " + authenticationResponse.getStatusCodeValue());
			if (authenticationResponse.getStatusCodeValue() == 200) {
				logger.trace("Inside if block..");
				return ResponseEntity.status(HttpStatus.OK).build().of(Optional.of(authenticationResponse.getBody()));
			} else {
				logger.trace("Inside else block..");
				ErrorResponse authenticationErrorResponse = gson
						.fromJson(jsonBodyGenerator.getJsonBody(authenticationResponse.getBody()), ErrorResponse.class);
				logger.info("Authentication Error Response : " + gson.toJson(authenticationErrorResponse));

				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
						.of(Optional.of(authenticationErrorResponse));
			}
		} catch (Exception e) {
			logger.error("Exception caught in authenticateUser() method: " + e);
			JsonElement je = JsonParser.parseString(jsonBodyGenerator.getJsonBody(e.getMessage()));
			String json = gson.toJson(je);
			return ResponseEntity.status(jsonBodyGenerator.getStatusCode(e.getMessage())).body(json);
		} finally {

		}
	}

	/**
	 * @param orderDTO
	 * @param authHeader
	 * @return Job status response with random numeric run id, Alphanumeric run id, and job status url
	 * 		   1. Append the "AZ_RUN_ID"
	 * 		   2. Hit the run order API
	 * 		   3. Hit the job status API
	 *         4. capture Folder ID and folder name from job status response
	 *         5. capture the SLA Job id and map it with random numeric run id and store the mapping in mapping file.
	 * @throws Exception
	 */
	@SuppressWarnings({ "null", "deprecation" })
	public ResponseEntity<Object> sendNumericRunId(OrderDTO orderDTO, String authHeader) throws Exception {
		logger.trace("sendNumericRunId() method accessed..");
		ResponseEntity<Object> runOrderResponse = null;
		try {
			logger.trace("Inside try block of sendNumericRunId() method...");
			ArrayList<HashMap<String, String>> variables = orderDTO.getVariables();
			HashMap<String, String> runIdMap = new HashMap<>();
			String numericRunId = null;
			Order order = modelMapper.map(orderDTO, Order.class);
			if (variables == null) {
				variables = new ArrayList<>();
				numericRunId = RandomRunIdGenerator.generateRandomRunId();
				runIdMap.put("AZ_RUN_ID", numericRunId);
				variables.add(runIdMap);
				order.setVariables(variables);
			} // if
			else {
				numericRunId = RandomRunIdGenerator.generateRandomRunId();
				runIdMap.put("AZ_RUN_ID", numericRunId);
				variables.add(runIdMap);
				order.setVariables(variables);
			} // else

			String orderDetails = gson.toJson(order);
			HttpHeaders orderDetailsHeaders = HttpHeaderGenerator.getHeaders();
			orderDetailsHeaders.set("Authorization", authHeader);
			logger.info("Headers : " + orderDetailsHeaders.toString());
			HttpEntity<String> orderDetailsEntity = new HttpEntity<String>(orderDetails, orderDetailsHeaders);
			logger.info("Order Details Entity : " + orderDetailsEntity.toString());
			String RUN_ORDER_URL = urlBuilder.buildRunOrderURL();
			logger.info("RUN_ORDER_URL : " + RUN_ORDER_URL);
			runOrderResponse = restTemplate.exchange(RUN_ORDER_URL, HttpMethod.POST, orderDetailsEntity, Object.class);
			logger.info("Run Order Response Body: " + runOrderResponse.getBody());
			logger.info("Run Order Response Code value: " + runOrderResponse.getStatusCodeValue());
			if (runOrderResponse.getStatusCodeValue() == 200) {
				logger.trace("Inside if block...");
				OrderResponse orderResponse = gson.fromJson(gson.toJson(runOrderResponse.getBody()),
						OrderResponse.class);
				jobStatus.setAlphanumericRunId(orderResponse.getRunId());
				jobStatus.setStatusURI(orderResponse.getStatusURI());
				jobStatus.setNumericRunId(Integer.parseInt(numericRunId));
				String lastJobId = captureFolderIdAndFolderNamebyJobStatusApi(numericRunId, authHeader,
						jobStatus.getAlphanumericRunId());
				logger.info("Last job id from Job Status Api is: " + lastJobId);
				runIdPersister.saveRunIdAndLastJobId(jobStatus.getNumericRunId().toString(), lastJobId);
				logger.info("Final Job status Response after appending run ID : " + gson.toJson(jobStatus));
				logger.info("Final response Entity : " + ResponseEntity.ok(jobStatus).toString());
				return ResponseEntity.ok(jobStatus);
			} else {
				logger.trace("Inside Else block..");
				return runOrderResponse;
			}
		} catch (Exception e) {
			logger.error("Exception caught in sendNumericRunId() method.. : " + e.getMessage());
			if (jsonBodyGenerator.getStatusCode(e.getMessage()) == 401) {
				ErrorResponse errorResponse = new ErrorResponse();
				Error error = new Error();
				error.setMessage("User not found. Session token is invalid or expired.");
				ArrayList<Error> errorList = new ArrayList<>();
				errorList.add(error);
				errorResponse.setErrors(errorList);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(gson.toJson(errorResponse));
			}
			return ResponseEntity.status(jsonBodyGenerator.getStatusCode(e.getMessage()))
					.body(jsonBodyGenerator.getJsonBody(e.getMessage()));

		} finally {

		}

	}

	
	/**
	 * @param numericRunId
	 * @param authHeader
	 * @param alphanumericRunId
	 * @return The SLA Job id to parent method
	 * 		   1. Hit the Run Jobs API with foldername as SLA_ControlM_<FOlder name>
	 * 		   2. Capture the job id corresponding to the folder id
	 * 		   3. Return the SLA job id
	 * @throws Exception
	 */
	private String captureFolderIdAndFolderNamebyJobStatusApi(String numericRunId, String authHeader,
			String alphanumericRunId) throws Exception {
		logger.info("captureFolderIdAndFolderNamebyJobStatusApi() method accessed...");
		String folderId = null;
		String folderName = null;
		String jobId = null;
		try {
			HttpHeaders jobStatusHeaders = HttpHeaderGenerator.getHeaders();
			jobStatusHeaders.set("Authorization", authHeader);
			logger.info("Required Header : " + jobStatusHeaders.toString());
			HttpEntity<String> jobStatusEntity = new HttpEntity<String>(jobStatusHeaders);
			logger.info("Required Http Entity : " + jobStatusEntity.toString());
			StringBuffer sb = new StringBuffer();
			String JOB_STATUS_URL = sb.append(urlBuilder.buildJobStatusURL()).append(alphanumericRunId).toString();
			logger.info("JOB_STATUS_URL :" + JOB_STATUS_URL);
			JobStatusResponse jobStatusResponse = retryService.retryJobStatusResponseAPI(alphanumericRunId, authHeader);
			ArrayList<Status> statuses = jobStatusResponse.getStatuses();
			logger.info("Statuses from Job Status Response: " + gson.toJson(statuses));
			Iterator<Status> statusesIterator = jobStatusResponse.getStatuses().iterator();
			while (statusesIterator.hasNext()) {
				Status status = statusesIterator.next();
				if (status.getType().equals("Folder")) {
					folderId = status.getJobId();
					folderName = status.getName();
					logger.info("folder Id : " + folderId + " folder Name : " + folderName);
					if (folderId != null && folderName != null) {
						jobId = retryService.retryCaptureJobIdByRunJobsAPI(authHeader, folderId, folderName);
						logger.info("Job id from RunJobsApi is : " + jobId);
					}
					break;
				}
			}
			return jobId;
		} catch (Exception e) {
			logger.error(
					"Exception caught in captureFolderIdAndFolderNamebyJobStatusApi() method.. : " + e.getMessage());
			logger.info(ResponseEntity.status(jsonBodyGenerator.getStatusCode(e.getMessage()))
					.body(jsonBodyGenerator.getJsonBody(e.getMessage())).toString());

			throw e;
		}
	}

	/**
	 * @param numericRunId
	 * @param authHeader
	 * @return the Job status (SLA_job) from SLA status URL from random numeric run id
	 */
	@Retryable(retryFor = { Exception.class }, maxAttempts = 3, backoff = @Backoff(delay = 8000))
	public Object sendJobStatusBySLAApi(String numericRunId, String authHeader) {

		try {
			HttpHeaders SLAHeaders = HttpHeaderGenerator.getHeaders();
			SLAHeaders.set("Authorization", authHeader);
			logger.info("Required Header : " + SLAHeaders.toString());
			HttpEntity<String> SLAEntity = new HttpEntity<String>(SLAHeaders);
			logger.info("Required Http Entity : " + SLAEntity.toString());
			String SLA_SERVICES_URL = urlBuilder.buildSLAServicesURL();
			logger.info("SLA_SERVICES_URL :" + SLA_SERVICES_URL);
			SLAServices slaServiceResponse = restTemplate
					.exchange(SLA_SERVICES_URL, HttpMethod.GET, SLAEntity, SLAServices.class).getBody();
			logger.debug("Job Status SLA response : " + gson.toJson(slaServiceResponse));
			ArrayList<ActiveService> activeServices = slaServiceResponse.getActiveServices();
			String jobId = runIdPersister.getLastJobIdByNumericRunId(numericRunId);
			logger.info("JOB ID from mapping file is : " + jobId);
			if (jobId == null) {
				ErrorResponse errorResponse = new ErrorResponse();
				Error error = new Error();
				error.setMessage("Unknown run ID " + numericRunId);
				ArrayList<Error> errorList = new ArrayList<>();
				errorList.add(error);
				errorResponse.setErrors(errorList);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(gson.toJson(errorResponse));
			}
			ActiveService activeService1 = new ActiveService();
			for (ActiveService activeService : activeServices) {
				if (activeService.getServiceJob().equals(jobId)) {
					activeService1 = activeService;
				}
			}
			logger.info("Active service with SLA job : " + gson.toJson(activeService1));
			return ResponseEntity.ok(gson.toJson(activeService1));
		} catch (Exception e) {
			logger.error("Exception caught in sendJobStatusBySLAApi() method.. : " + e.getMessage());
			if (jsonBodyGenerator.getStatusCode(e.getMessage()) == 401
					|| jsonBodyGenerator.getStatusCode(e.getMessage()) == 500) {
				ErrorResponse errorResponse = new ErrorResponse();
				Error error = new Error();
				error.setMessage("User not found. Session token is invalid or expired.");
				ArrayList<Error> errorList = new ArrayList<>();
				errorList.add(error);
				errorResponse.setErrors(errorList);
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(gson.toJson(errorResponse));
			}
			return ResponseEntity.status(jsonBodyGenerator.getStatusCode(e.getMessage()))
					.body(jsonBodyGenerator.getJsonBody(e.getMessage()));
		}
	}
}
