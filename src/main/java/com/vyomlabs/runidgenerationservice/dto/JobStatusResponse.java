package com.vyomlabs.runidgenerationservice.dto;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class JobStatusResponse {
	@SerializedName("statuses")
	@Expose
	private ArrayList<Status> statuses = null;
	@SerializedName("startIndex")
	@Expose
	private int startIndex;
	@SerializedName("itemsPerPage")
	@Expose
	private int itemsPerPage;
	@SerializedName("total")
	@Expose
	private int total;
	@SerializedName("nextURI")
	@Expose
	private String nextURI;
}
