package com.mrtcn.bankingSystem.Models;

import org.apache.ibatis.type.Alias;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Alias("UserModel")
public class UserModel{
	
	private int id;
	private String username;
	private String password;
	private boolean isEnabled;
	private String authorities;

}
