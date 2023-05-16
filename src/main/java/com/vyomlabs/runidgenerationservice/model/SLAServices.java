package com.vyomlabs.runidgenerationservice.model;

import java.util.ArrayList;

import lombok.Data;

@Data
public class SLAServices {

	public String serviceLastUpdatedTime;
	public ArrayList<ActiveService> activeServices;

}
