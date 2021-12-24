package com.jameseng.dscatalog.dto;

import com.jameseng.dscatalog.services.validation.UserUpdateValid;

@UserUpdateValid //processa a verificação se o email inserido já existe no banco de dados
public class UserUpdateDTO extends UserDTO{
	private static final long serialVersionUID = 1L;
	
}
