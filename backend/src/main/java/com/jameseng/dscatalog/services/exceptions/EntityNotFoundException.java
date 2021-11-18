package com.jameseng.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException {

	static final long serialVersionUID = 1L;
	
	public EntityNotFoundException(String msg) {
		super(msg);
	}
}
