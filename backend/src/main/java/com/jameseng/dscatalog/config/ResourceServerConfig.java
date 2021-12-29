package com.jameseng.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer //processa a funcionalidade do Oauth2
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired
	private JwtTokenStore tokenStore;
	
	private static final String[] PUBLIC={"/oauth/token", "/h2-console/**"};  //Rota pública

	private static final String[] OPERATOR_OR_ADMIN={"/products/**","/categories/**"}; //Rota liberada para operador e administrador

	private static final String[] ADMIN={"/users/**"}; //Rota liberara somente para o admin
	
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore); //configurando o tokenstore e validando-o
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests().antMatchers(PUBLIC).permitAll()   		//rotas até public todos tem acesso liberado
		.antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll()		//libera todo o método get no vetor operator_or_admin, liberando pata todos
		.antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") //Só operador ou admin podem acessar rotas do vetor OPERATOR_OR_ADMIN
		.antMatchers(ADMIN).hasRole("ADMIN")							//só pode acessar ADMIN que logar como o perfil ADMIN
		.anyRequest().authenticated();									//qualquer outra rota, tem que estar logado para acessar, sem importar o perfil
	}
}
