package com.vyomlabs.runidgenerationservice.model;

import lombok.Data;

@Data
public class AuthenticationSuccessResponse {
	public String username;
	public String token;
	public String version;

}
