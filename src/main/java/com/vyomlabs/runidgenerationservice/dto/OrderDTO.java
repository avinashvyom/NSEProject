package com.vyomlabs.runidgenerationservice.dto;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class OrderDTO {
	@SerializedName("folder")
	@Expose
	private String folder;
	@SerializedName("application")
	@Expose
	private String application;
	@SerializedName("subapplication")
	@Expose
	private String subapplication;
	@SerializedName("ctm")
	@Expose
	private String ctm;
	@SerializedName("jobs")
	@Expose
	private String jobs;
	@SerializedName("ignoreCriteria")
	@Expose
	private String ignoreCriteria;
	@SerializedName("orderDate")
	@Expose
	private String orderDate;
	@SerializedName("hold")
	@Expose
	private String hold;
	@SerializedName("orderIntoFolder")
	@Expose
	private String orderIntoFolder;
	@SerializedName("waitForOrderDate")
	@Expose
	private String waitForOrderDate;
	@SerializedName("variables")
	@Expose
	private ArrayList<HashMap<String, String>> variables;

}
