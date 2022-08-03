package com.mrtcn.bankingSystem.Models;

import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@Alias("Account")
public class Account implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private long number;
    private String name;
    private String surname;
    private String tc;
    private String email;
    private int balance;
    private String type;
    private Instant lastUpdate;
    private boolean isDeleted;
    private int userId;

}
