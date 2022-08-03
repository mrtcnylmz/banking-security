package com.mrtcn.bankingSystem.Interfaces;

import com.mrtcn.bankingSystem.Requests.DepositRequest;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import com.mrtcn.bankingSystem.Responses.DeleteResponse;
import com.mrtcn.bankingSystem.Responses.DepositResponse;
import com.mrtcn.bankingSystem.Responses.GetAccountResponse;
import com.mrtcn.bankingSystem.Responses.TransferResponse;

public interface IAccountService {

	public AccountCreateResponse createAccount(NewAccountRequest request);
	
	public GetAccountResponse getAccount(Long id);
	
	public DeleteResponse delete(Long id);
	
	public DepositResponse deposit(Long id, DepositRequest request);
	
	public TransferResponse transfer(Long id, TransferRequest request);
}
