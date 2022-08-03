package com.mrtcn.bankingSystem.Requests;

public class TransferRequest {
	private long transferredAccountNumber;
	private int amount;
	
	public long getTransferredAccountNumber() {
		return transferredAccountNumber;
	}
	public void setTransferredAccountNumber(long transferredAccountNumber) {
		this.transferredAccountNumber = transferredAccountNumber;
	}
	
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}

}
