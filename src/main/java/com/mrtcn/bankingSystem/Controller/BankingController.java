package com.mrtcn.bankingSystem.Controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.mrtcn.bankingSystem.Interfaces.IAccountService;
import com.mrtcn.bankingSystem.Interfaces.ILogService;
import com.mrtcn.bankingSystem.Requests.DepositRequest;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import com.mrtcn.bankingSystem.Responses.DeleteResponse;
import com.mrtcn.bankingSystem.Responses.DepositResponse;
import com.mrtcn.bankingSystem.Responses.GetAccountResponse;
import com.mrtcn.bankingSystem.Responses.LogResponse;
import com.mrtcn.bankingSystem.Responses.TransferResponse;

@RestController
public class BankingController {
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private KafkaTemplate<String,String> producer;
	
	@Autowired
	private IAccountService accountService;
	
	//Get the authenticated username.
	@GetMapping("/auth")
	public ResponseEntity<?> authCheck(){
		
		String string = "User " + SecurityContextHolder.getContext().getAuthentication().getName() + " Has Auth.";
		
        return ResponseEntity
                .ok()
                .header("content-type","application/json")
                .body(string);
	}

	//1
	//A service to create a user account.
	@RequestMapping(path = "account/register", method = RequestMethod.POST)
	public ResponseEntity<AccountCreateResponse> createAccount(@RequestBody NewAccountRequest request){
		
		//Response from service.
		AccountCreateResponse response = this.accountService.createAccount(request);
		
		if(response.getMessage().equals("Account Created")) {
            return ResponseEntity
                    .ok()
                    .header("content-type","application/json")
                    .body(response);
		}else 
            return ResponseEntity
                    .badRequest()
                    .header("content-type","application/json")
                    .body(response);
	}

	//2
	//A service to get account information from account number.
	@RequestMapping(path = "account/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getAccount(@PathVariable Long id){
		
		//Response from service.
		GetAccountResponse response = this.accountService.getAccount(id);
		
		if(response.getMessage().equals("Success")) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.lastModified(response.getAccount().getLastUpdate())
					.body(response.getAccount());
			
		}else if (response.getMessage().equals("Invalid Account Id")) {
			return ResponseEntity
					.status(403)
					.header("content-type","application/json")
					.body(response);
			
		}else {
			return ResponseEntity
					.notFound()
					.build();
		}
	}

	//3
	//A service that takes an account number and handles monetary deposits accordingly.
	@RequestMapping(path = "/account/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> deposit(@PathVariable Long id, @RequestBody DepositRequest request) {
		
		//Response from service.
		DepositResponse response = this.accountService.deposit(id, request);
		
		if(response.getMessage().equals("Success")) {
			String log =
					response.getAccount().getNumber() + " " +
					"deposit" + " " + "amount:" + " " +
					request.getAmount() + " " +
					response.getAccount().getType();

			//Log send to kafka.
			producer.send("logs",log);
			
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.lastModified(response.getAccount().getLastUpdate())
					.body(response.getAccount());
			
		}else if (response.getMessage().equals("Invalid Account Id")) {
			return ResponseEntity
					.status(403)
					.header("content-type","application/json")
					.body(response);
			
		}else if (response.getMessage().equals("Invalid amount.")) {
			return ResponseEntity
					.badRequest()
					.header("content-type","application/json")
					.body(response);
			
		}else {
			return ResponseEntity
					.notFound()
					.build();
		}
	}

	//4
	//A service to make balance transfers between accounts.
	@RequestMapping(path = "/account/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<TransferResponse> transfer(@PathVariable Long id, @RequestBody TransferRequest request) {
		
		//Response from service.
		TransferResponse response = this.accountService.transfer(id, request);
		
		if (response == null) {
			return ResponseEntity
					.notFound()
					.header("content-type", "application/json")
					.build();
		}else if(response.getMessage().equals("Transferred Successfully.")) {
			
			String log =
					id + " " +
					"transfer_amount:" + " " +
					request.getAmount() + " " + 
					"transferred_account:" + " " +
					request.getTransferredAccountNumber() + " " +
					response.getAccount().getType();

			//Log sent to kafka.
			producer.send("logs", log);
			
			return ResponseEntity
					.ok()
					.header("content-type", "application/json")
					.body(response);
			
		}else{
			return ResponseEntity
					.badRequest()
					.header("content-type", "application/json")
					.body(response);
			
		}
	}

	//5
	//A service to access logs received and logged by kafka.
	@CrossOrigin(origins = {"http://localhost:6162"})
	@RequestMapping(path = "account/{id}/logs", method = RequestMethod.GET)
	public ResponseEntity<List<LogResponse>> getLog(@PathVariable Long id){
		
		//Response from service.
		List<LogResponse> response = this.logService.getLog(id);
		
		if(response != null) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.body(response);
			
		}else
			return ResponseEntity
					.notFound()
					.header("content-type","application/json")
					.build();
	}
	
	//6
	//A service to delete(soft delete) an account.
	@RequestMapping(path = "/account/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<DeleteResponse> delete(@PathVariable Long id){
		
		//Response from service.
		DeleteResponse response = this.accountService.delete(id);
		
		if (response != null) {
			return ResponseEntity
					.ok()
					.header("content-type","application/json")
					.body(response);
		}else {
			return ResponseEntity
					.notFound()
					.header("content-type","application/json")
					.build();
		}
	}
}
