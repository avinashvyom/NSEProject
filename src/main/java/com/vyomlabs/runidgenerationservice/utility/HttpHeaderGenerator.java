package com.vyomlabs.runidgenerationservice.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpHeaderGenerator {
	static Logger logger = LoggerFactory.getLogger(FileUtility.class);

	public static HttpHeaders getHeaders() {
		logger.trace("inside getHeaders method...");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		return headers;
	}

}
