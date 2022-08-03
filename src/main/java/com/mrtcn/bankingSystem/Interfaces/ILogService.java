package com.mrtcn.bankingSystem.Interfaces;

import java.util.List;

import com.mrtcn.bankingSystem.Responses.LogResponse;

public interface ILogService {
	
	public List<LogResponse> getLog(Long id);
	
}
