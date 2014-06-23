package com.tinyj.infra.exception;

@SuppressWarnings("serial")
public class FileDeleteErrorException extends CodedException
{
	public FileDeleteErrorException(String aMsg, int aErrorCode)
	{
		super(aMsg, aErrorCode);
	}

}
