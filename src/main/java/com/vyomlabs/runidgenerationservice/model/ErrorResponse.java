package com.vyomlabs.runidgenerationservice.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class ErrorResponse {
	public ArrayList<Error> errors;
}
