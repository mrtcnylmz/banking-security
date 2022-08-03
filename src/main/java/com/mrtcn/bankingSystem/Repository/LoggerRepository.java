package com.mrtcn.bankingSystem.Repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.mrtcn.bankingSystem.Models.Log;

@Mapper
public interface LoggerRepository {

	public void insertLog(Log log);
	
	public List<Log> selectLogWithId(Long id);
	
}
