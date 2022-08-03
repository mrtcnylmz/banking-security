package com.mrtcn.bankingSystem.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Interfaces.ILogService;
import com.mrtcn.bankingSystem.Models.Log;
import com.mrtcn.bankingSystem.Repository.LoggerRepository;
import com.mrtcn.bankingSystem.Responses.LogResponse;

@Component
public class LogService implements ILogService{
	
	private long accountId;
	private String process;
	private int amount;
	private long transferedAccountId;
	private String type;
	
    @Autowired
    private LoggerRepository repository;

	@Override
	public List<LogResponse> getLog(Long id) {
		
		List<LogResponse> logList = new ArrayList<>();
		
		//Logs taken from database.
		List<Log> log = this.repository.selectLogWithId(id);
		
		if (log == null) {
			return null;
			
		}else {
			//Each log parsed into easy to understand sentences.
			log.forEach(new Consumer<Log>() {

				@Override
				public void accept(Log t) {
					
					accountId = t.getAccountId();
					process = t.getProcess();
					amount = t.getAmount();
					transferedAccountId = t.getTransferedAccountId();
					type = t.getType();
					
					if (process.equals("transfer_amount:")){
						//2781337413 hesabından 4258262637 numaralı hesaba 1112 TL transfer edilmiştir.
						String message = accountId + " hesabından " + transferedAccountId + " numaralı hesaba " + amount + " " + type +" transfer edilmiştir.";
						logList.add(LogResponse.builder().log(message).build());
						
					} else if (process.equals("deposit")) {
						//2781337413 numaralı hesaba 1112 TL yatırılmıştır.
						String message = accountId + " numaralı hesaba " + amount + " " + type + " yatırılmıştır.";
						logList.add(LogResponse.builder().log(message).build());
					}
				}
			});
			
			return logList;
		}
		
		
	}
}
