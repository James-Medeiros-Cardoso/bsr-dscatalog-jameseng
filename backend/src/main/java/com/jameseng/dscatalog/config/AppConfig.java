package com.jameseng.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {
	
	@Bean //componente do spring = será um componente gerenciado pelo springboot
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(); //instanciado
	}
	// vamos injetar o component BCryptPasswordEncoder em outras classes
}
