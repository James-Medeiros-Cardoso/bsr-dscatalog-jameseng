package com.jameseng.dscatalog.services.exceptions;

public class DatabaseException extends RuntimeException {

	static final long serialVersionUID = 1L;
	
	public DatabaseException(String msg) {
		super(msg);
	}
}
