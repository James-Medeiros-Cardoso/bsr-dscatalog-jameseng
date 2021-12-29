package com.jameseng.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
public class AppConfig {
	
	@Bean //componente do spring = será um componente gerenciado pelo springboot
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(); //instanciado
	}
	// vamos injetar o component BCryptPasswordEncoder em outras classes
	
	
	//Objetos capazes de acessar (ler, codificar, criar) um tokem JWT: -----------------------------------------------
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter(); //instanciou o obejto
		tokenConverter.setSigningKey("MY-JWT-SECRET"); //registra a chave do token (assinatura)
		return tokenConverter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
}
