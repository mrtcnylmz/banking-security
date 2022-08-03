package com.mrtcn.bankingSystem.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mrtcn.bankingSystem.Requests.LoginRequest;
import com.mrtcn.bankingSystem.Services.MyBatisUserDetailsService;
import com.mrtcn.bankingSystem.Util.JWTTokenUtil;

@RestController
public class LoginController {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTTokenUtil jwtTokenUtil;
	
	@Autowired
	private MyBatisUserDetailsService myBatisUserDetailsService;
	
	//Auth path from user login.
	@PostMapping("/auth")
	public ResponseEntity<?> auth(@RequestBody LoginRequest request) {
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
		} catch (BadCredentialsException e) {
			return ResponseEntity.badRequest().build();
		}catch (DisabledException e) {
			// TODO: handle exception
		}
		
		final UserDetails userDetails = myBatisUserDetailsService.loadUserByUsername(request.getUsername());
		
		final String token = jwtTokenUtil.generateToken(userDetails);
		
		return ResponseEntity
				.ok()
				.body(token);
	}
}
