package com.mrtcn.bankingSystem.Repository;

import java.time.Instant;

import org.apache.ibatis.annotations.Mapper;

import com.mrtcn.bankingSystem.Models.Account;

@Mapper
public interface AccountRepository {
	
	public void insertAccountWithNumber(Account account);
	
	public Account selectAccountWithNumber(Long id);
	
	public void updateAccountBalance(Long id, int amount);
	
	public void updateAccountLastUpdate(Long id, Instant date);
	
	public void updateAccountIsDeleted(Long id, boolean isDeleted);
	
	public String test(int id);
	
}
