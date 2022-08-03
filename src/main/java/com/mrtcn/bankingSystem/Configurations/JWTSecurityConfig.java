package com.mrtcn.bankingSystem.Configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.mrtcn.bankingSystem.Filter.JwtRequestFilter;
import com.mrtcn.bankingSystem.Services.MyBatisUserDetailsService;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private JwtRequestFilter jwtRequestFilter;
	
	@Autowired
	private MyBatisUserDetailsService myBatisUserDetailsService;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth
		.userDetailsService(myBatisUserDetailsService)
		.passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception{
		return super.authenticationManagerBean();
	}
	
	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity
		.csrf()
		.disable()
		.authorizeHttpRequests()
		.antMatchers(HttpMethod.POST, "/auth")
		.permitAll()
		.antMatchers(HttpMethod.POST, "/account/register")
		.hasAuthority("CREATE_ACCOUNT")
		.antMatchers(HttpMethod.GET, "/account/**")
		.authenticated()
		.antMatchers(HttpMethod.POST, "/account/**")
		.authenticated()
		.antMatchers(HttpMethod.PATCH, "/account/**")
		.authenticated()
		.antMatchers(HttpMethod.DELETE, "/account/**")
		.hasAuthority("REMOVE_ACCOUNT")
		.anyRequest()
		.authenticated()
		.and()
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
