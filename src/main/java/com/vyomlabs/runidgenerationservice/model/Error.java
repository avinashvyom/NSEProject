package com.vyomlabs.runidgenerationservice.model;

import com.google.gson.annotations.Expose;

import lombok.Data;

@Data
public class Error {

	@Expose
	public String message;

	public String id;
	
	public String item;
	
	public String file;

	public String code;

	public String Uri;

	public Integer line;

	public Integer col;

}
