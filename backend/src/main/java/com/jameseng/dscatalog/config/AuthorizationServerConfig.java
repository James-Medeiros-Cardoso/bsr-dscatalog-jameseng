package com.jameseng.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.jameseng.dscatalog.components.JwtTokenEnhancer;

@Configuration //classe de configuração
@EnableAuthorizationServer //essa clase representa o autorizationServer do OAuth2
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	@Value("${jwt.duration}")
	private Integer jwtDuration;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;

	@Autowired
	private JwtTokenStore tokenStore;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenEnhancer tokenEnhancer;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		clients.inMemory()								//processo feito em memória
		.withClient(clientId)							//difinir o cliente id
		.secret(passwordEncoder.encode(clientSecret))	//definir a senha da aplicação: dscatalog123;
		.scopes("read", "write")						//tipo de acesso: litura e escrita
		.authorizedGrantTypes("password")
		.accessTokenValiditySeconds(jwtDuration);		//tempo de expiração do Token (em segundos) | 86400 = 24 horas
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		TokenEnhancerChain chain=new TokenEnhancerChain();
		chain.setTokenEnhancers(Arrays.asList(accessTokenConverter, tokenEnhancer));
		
		endpoints.authenticationManager(authenticationManager) 	//autenticação
		.tokenStore(tokenStore)									//objetos que vão procesar o token
		.accessTokenConverter(accessTokenConverter)
		.tokenEnhancer(chain);
	}
}
