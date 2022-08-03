package com.mrtcn.bankingSystem.Requests;

import lombok.Data;

@Data
public class LoginRequest {
	private String username;
	private String password;
}
