package com.vyomlabs.runidgenerationservice.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.vyomlabs.runidgenerationservice.dto.OrderDTO;
import com.vyomlabs.runidgenerationservice.model.LoginRequest;
import com.vyomlabs.runidgenerationservice.service.OrderService;

@RestController
public class OrderController {

	@Autowired
	public OrderService orderService;

	Logger logger = LoggerFactory.getLogger(OrderController.class);

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public OrderController(OrderService orderService) {
		super();
		this.orderService = orderService;
	}

	public OrderController() {
		super();
	}

	@PostMapping("/login")
	public ResponseEntity<Object> authenticate(@RequestBody LoginRequest loginRequest)
			throws JsonSyntaxException, JsonProcessingException {
		logger.trace("Authenticate Method accessed..");
		logger.info("Login details : " + gson.toJson(loginRequest));
		return orderService.authenticateUser(loginRequest);
	}

	@PostMapping("/run/order")
	public ResponseEntity<Object> sendNumericRunId(@RequestBody OrderDTO order,
			@RequestHeader("Authorization") String authHeader) throws Exception {
		logger.trace("sendNumericRunId method accessed..");
		// logger.info("Order Details :" + gson.toJson(order));
		return orderService.sendNumericRunId(order, authHeader);
	}

	@GetMapping("/run/order/status/{numericRunId}")
	public Object sendJobStatusByNumericRunId(@PathVariable(value = "numericRunId") String numericRunId,
			@RequestHeader("Authorization") String authHeader) throws IOException {
		logger.trace("sendJobStatusByNumericRunId method accessed..");
		logger.info("Numeric Run Id from request :" + numericRunId);
		return orderService.sendJobStatusBySLAApi(numericRunId, authHeader);
	}

}
