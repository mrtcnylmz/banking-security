package com.mrtcn.bankingSystem.Services;

import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.mrtcn.bankingSystem.Interfaces.IAccountService;
import com.mrtcn.bankingSystem.Models.Account;
import com.mrtcn.bankingSystem.Repository.AccountRepository;
import com.mrtcn.bankingSystem.Repository.UserRepository;
import com.mrtcn.bankingSystem.Requests.DepositRequest;
import com.mrtcn.bankingSystem.Requests.NewAccountRequest;
import com.mrtcn.bankingSystem.Requests.TransferRequest;
import com.mrtcn.bankingSystem.Responses.AccountCreateResponse;
import com.mrtcn.bankingSystem.Responses.DeleteResponse;
import com.mrtcn.bankingSystem.Responses.DepositResponse;
import com.mrtcn.bankingSystem.Responses.GetAccountResponse;
import com.mrtcn.bankingSystem.Responses.TransferResponse;

@Component
public class AccountService implements IAccountService{
	
    @Autowired
    private AccountRepository accountRepository;
    
	@Autowired
	private UserRepository userRepository;

	//1
	//A service to create a user account.
	@Override
	public AccountCreateResponse createAccount(NewAccountRequest request) {
		
		//Response init.
		AccountCreateResponse response = AccountCreateResponse.builder().build();
		
        //Account type checks made here.
        if (!(request.getType().equals("TL") || request.getType().equals("Dolar") || request.getType().equals("Altın"))){
            response.setMessage("Invalid Account Type: " + request.getType());
            return response;
        }

        //10-Digit Random number for account number.
        long randomNumber = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;

        //Account object gets build.
        Account acc = Account.builder()
                .number(randomNumber)
                .type(request.getType())
                .email(request.getEmail())
                .tc(request.getTc())
                .name(request.getName())
                .surname(request.getSurname())
                .isDeleted(false)
                .lastUpdate(ZonedDateTime.now().toInstant())
                .userId(userRepository.selectUserIdWithName(SecurityContextHolder.getContext().getAuthentication().getName()))
                .build();

        //Account gets inserted into database.
        this.accountRepository.insertAccountWithNumber(acc);
        
        //A response for json response body.
        response.setAccountNumber(randomNumber);
        response.setMessage("Account Created");
        return response;
	}

	//2
	//A service to get account information from account number.
	@Override
	public GetAccountResponse getAccount(Long id) {
		
		//Account data from database.
		Account account = this.accountRepository.selectAccountWithNumber(id);
		
		//Response init.
		GetAccountResponse response = GetAccountResponse.builder().build();
		
		//Checks if account exist or deleted.
		if(account == null || account.isDeleted()) {
			response.setMessage("Error! Either account is deleted or does not exists.");
			return response;
			
		}else if (account.getUserId() == userRepository.selectUserIdWithName(SecurityContextHolder.getContext().getAuthentication().getName())) {
			response.setAccount(account);
			response.setMessage("Success");
			return response;
			
		}else {
			response.setMessage("Invalid Account Id");
			return response;
		}
	}

	//3
	//A service that takes an account number and handles monetary deposits accordingly.
	@Override
	public DepositResponse deposit(Long id, DepositRequest request) {
		
		//Account data from database.
		Account account = this.accountRepository.selectAccountWithNumber(id);
		
		//Response init.
		DepositResponse response = DepositResponse.builder().build();
		
		//Check if account exist or deleted.
		if (account == null || account.isDeleted()) {
			response.setMessage("Error! Either account is deleted or does not exists.");
			return response;
		
		//Check if account owner and user have the same Id's.	
		}else if (account.getUserId() == userRepository.selectUserIdWithName(SecurityContextHolder.getContext().getAuthentication().getName())) {
			
			//Valid amount check.
			if (request.getAmount() <= 0) {
				response.setMessage("Invalid amount.");
				return response;
				
			}else {
				//New amount.
				int amount = account.getBalance() + request.getAmount();
				
				//Database updates.
				this.accountRepository.updateAccountBalance(id, amount);
				this.accountRepository.updateAccountLastUpdate(id, ZonedDateTime.now().toInstant());
				
				account.setBalance(amount);
				
				response.setAccount(account);
				response.setMessage("Success");
				return response;
			}
			
		//Existing account has different Id from logged user.
		}else {
			response.setMessage("Invalid Account Id");
			return response;
		}
	}
	
	//4
	//A service to make balance transfers between accounts.
	@Transactional(isolation =Isolation.SERIALIZABLE)
	@Override
	public TransferResponse transfer(Long id, TransferRequest request) {
		
		//Response init.
		TransferResponse response = TransferResponse.builder().build();

		//Account numbers.
		long senderAccountNumber = id;
		long receiverAccountNumber = request.getTransferredAccountNumber();
		
		//Accounts.
		Account senderAccount = this.accountRepository.selectAccountWithNumber(senderAccountNumber);
		Account receiverAccount = this.accountRepository.selectAccountWithNumber(receiverAccountNumber);
		
		//Checks if accounts exists or deleted.
		if (senderAccount == null || receiverAccount == null || senderAccount.isDeleted() || receiverAccount.isDeleted()) {
			response.setMessage("Accounts are either deleted or does not exist.");
			return response;
		}
		
		if (senderAccount.getUserId() != userRepository.selectUserIdWithName(SecurityContextHolder.getContext().getAuthentication().getName())) {
			response.setMessage("Invalid Account Id");
			return response;
		}
		
		//Balances and amounts.
		int senderBalance = senderAccount.getBalance();
		int receiverBalance = receiverAccount.getBalance();
		int transferAmount = request.getAmount();
		
		//Checks if sender account has enough balance to send required amount.
		if (senderBalance < transferAmount || transferAmount <= 0) {
			response.setMessage("Insufficient/Invalid balance amount.");
			return response;
		}
		
		//If sender and receiver accounts belong to different account types then CurrencyExchange service is necessary to convert between currency types.
		if (!(senderAccount.getType().equals(receiverAccount.getType()))){
			//Database processes on sender account.
			int senderNewBalance = senderAccount.getBalance() - transferAmount;
			this.accountRepository.updateAccountBalance(senderAccountNumber, senderNewBalance);
			this.accountRepository.updateAccountLastUpdate(senderAccountNumber, ZonedDateTime.now().toInstant());
			
			//Database processes on receiver account.
			int receiverNewBalance = receiverBalance + (int)(transferAmount * (new CurrencyExchange().Exchange(senderAccount,receiverAccount)));
			this.accountRepository.updateAccountBalance(receiverAccountNumber, receiverNewBalance);
			this.accountRepository.updateAccountLastUpdate(receiverAccountNumber, ZonedDateTime.now().toInstant());

			response.setMessage("Transferred Successfully.");
			response.setAccount(senderAccount);
			return response;
			
		}else {	//If accounts are same type.
			
			//Database processes on sender account.
			int senderNewBalance = senderBalance - request.getAmount();
			this.accountRepository.updateAccountBalance(senderAccountNumber, senderNewBalance);
			this.accountRepository.updateAccountLastUpdate(senderAccountNumber, ZonedDateTime.now().toInstant());
			
			//Database processes on receiver account.
			int receiverNewBalance = receiverBalance + request.getAmount();
			this.accountRepository.updateAccountBalance(receiverAccountNumber, receiverNewBalance);
			this.accountRepository.updateAccountLastUpdate(receiverAccountNumber, ZonedDateTime.now().toInstant());
			
			response.setMessage("Transferred Successfully.");
			response.setAccount(senderAccount);
			return response;
		}
	}
	
	//6
	//A service to delete(soft delete) an account.
	@Override
	public DeleteResponse delete(Long id) {
		//Account data fetched with account id from database.
		Account account = this.accountRepository.selectAccountWithNumber(id);
		
		//Check if account exists in database, if not, return accordingly.
		if (account == null || account.isDeleted()) {
			return null;
			
		}else {
			
			//Soft delete and account update date send to database.
			this.accountRepository.updateAccountIsDeleted(id, true);
			this.accountRepository.updateAccountLastUpdate(id, ZonedDateTime.now().toInstant());
			
			//Response and its message.
			DeleteResponse response = DeleteResponse.builder()
					.message("Account has been successfully deleted!")
					.build();
			return response;
		}
	}
}
