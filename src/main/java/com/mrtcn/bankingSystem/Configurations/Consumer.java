package com.mrtcn.bankingSystem.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.mrtcn.bankingSystem.Models.Log;
import com.mrtcn.bankingSystem.Repository.LoggerRepository;

@Component
public class Consumer {
	
	private long accountId;
	private String process;
	private int amount;
	private long transferedAccountId;
	private String type;
	
    @Autowired
    private LoggerRepository repository;

    //Kafka listens for logs.
	@KafkaListener(topics = {"logs"}, groupId = "logs_group")
    public void listenTransfer(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition
    ){
		
		//Parse the message to use.
		String[] separated = message.split(" ");
		//Transfer processes.
		if (separated[1].equals("transfer_amount:")){
			//Example:
			//2781337413 transfer_amount: 1112 transferred_account: 4258262637 TL
			
			accountId = Long.parseLong(separated[0]);
			process = separated[1];
			amount = Integer.parseInt(separated[2]);
			transferedAccountId = Long.parseLong(separated[4]);
			type = separated[5];
			
			//Deposit processes.
		} else if (separated[1].equals("deposit")) {
			//Example:
			//2781337413 deposit amount: 1112 TL
			
			accountId = Long.parseLong(separated[0]);
			process = separated[1];
			amount = Integer.parseInt(separated[3]);
			transferedAccountId = -1;
			type = separated[4];
		}
		
		//Log building.
		Log log = Log.builder()
				.accountId(accountId)
				.process(process)
				.amount(amount)
				.transferedAccountId(transferedAccountId)
				.type(type)
				.build();
		
		//Sending log to database.
		this.repository.insertLog(log);
    }
}
