package com.mrtcn.bankingSystem.Requests;

import java.time.Instant;

public class AccountRequest {
	private long number;
	private int balance;
    private String name;
    private String surname;
    private String email;
    private String tc;
    private String type;
    private Instant lastUpdate;
    private int userId;
    
	public long getNumber() {
		return number;
	}
	public void setNumber(long number) {
		this.number = number;
	}
	
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTc() {
		return tc;
	}
	public void setTc(String tc) {
		this.tc = tc;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public Instant getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Instant lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
