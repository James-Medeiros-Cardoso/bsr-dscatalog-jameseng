package com.jameseng.dscatalog.dto;

import com.jameseng.dscatalog.services.validation.UserInsertValid;

@UserInsertValid //processa a verificação se o email inserido já existe no banco de dados
public class UserInsertDTO extends UserDTO{

	private static final long serialVersionUID = 1L;
	
	private String password;

	public UserInsertDTO(){
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}	
}
