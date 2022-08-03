package com.mrtcn.bankingSystem.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mrtcn.bankingSystem.Models.UserModel;
import com.mrtcn.bankingSystem.Repository.UserRepository;

@Service
public class MyBatisUserDetailsService implements UserDetailsService{
	
    @Autowired
    private UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//Get data from database into userModel.
		UserModel userModel = repository.selectUserWithName(username);
		
		//Turn "authorities" String into List<GrantedAuthority> with for loop.
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		String[] separated = userModel.getAuthorities().split(",");

		for (String s: separated) {           
			authorities.add(new SimpleGrantedAuthority(s)); 
		}
		
		//Return User.
		return new org.springframework.security.core.userdetails
	            .User(userModel.getUsername(),userModel.getPassword(),authorities);
	}
}
