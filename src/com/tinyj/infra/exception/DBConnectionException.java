package com.tinyj.infra.exception;


@SuppressWarnings("serial")
public class DBConnectionException extends BaseException 
{
	public DBConnectionException(String aMsg)
	{
		super(aMsg);
	}

}
