package com.mrtcn.bankingSystem.Responses;

import com.mrtcn.bankingSystem.Models.Account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAccountResponse {
	
	private Account account;
    private String message;
    
}
