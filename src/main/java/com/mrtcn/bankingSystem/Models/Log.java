package com.mrtcn.bankingSystem.Models;

import org.apache.ibatis.type.Alias;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Alias("Log")
public class Log {

	private long id;
	private long accountId;
	private String process;
	private int amount;
	private long transferedAccountId;
	private String type;
	
}