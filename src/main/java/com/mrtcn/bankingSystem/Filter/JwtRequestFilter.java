package com.mrtcn.bankingSystem.Filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mrtcn.bankingSystem.Services.MyBatisUserDetailsService;
import com.mrtcn.bankingSystem.Util.JWTTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter{
	
	@Autowired
	private MyBatisUserDetailsService jwtUserDetailsService;
	
	@Autowired
	private JWTTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		//Takes "Authorization" header.
		final String requestTokenHeader = request.getHeader("Authorization");
		
		String username = null;
		String jwtToken = null;
		
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			
			//Takes just token from header.
			jwtToken = requestTokenHeader.substring(7);
			try {
				//Looks for a username inside encoded token.
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
			}catch (ExpiredJwtException e) {
				// TODO: handle exception
			}
			
		}else {
			logger.warn("Jwt token is problematic");
		}
		
		
		if (username != null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
			//Checks if token values and data from database mathes.
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		filterChain.doFilter(request, response);
	}

}
