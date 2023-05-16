package com.vyomlabs.runidgenerationservice.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Status {
	@SerializedName("jobId")
	@Expose
	private String jobId;
	@SerializedName("folderId")
	@Expose
	private String folderId;
	@SerializedName("numberOfRuns")
	@Expose
	private int numberOfRuns;
	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("type")
	@Expose
	private String type;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("held")
	@Expose
	private boolean held;
	@SerializedName("deleted")
	@Expose
	private boolean deleted;
	@SerializedName("startTime")
	@Expose
	private String startTime;
	@SerializedName("endTime")
	@Expose
	private String endTime;
	@SerializedName("outputURI")
	@Expose
	private String outputURI;
	@SerializedName("logURI")
	@Expose
	private String logURI;
	@SerializedName("folder")
	@Expose
	private String folder;
}
