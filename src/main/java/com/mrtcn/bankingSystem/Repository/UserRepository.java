package com.mrtcn.bankingSystem.Repository;

import org.apache.ibatis.annotations.Mapper;
import com.mrtcn.bankingSystem.Models.UserModel;

@Mapper
public interface UserRepository {

	public UserModel selectUserWithName(String username);
	
	public int selectUserIdWithName(String username);
	
}
