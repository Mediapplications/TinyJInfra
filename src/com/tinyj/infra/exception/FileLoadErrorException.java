package com.tinyj.infra.exception;

@SuppressWarnings("serial")
public class FileLoadErrorException extends CodedException
{
	public FileLoadErrorException(String aMsg, int aErrorCode)
	{
		super(aMsg, aErrorCode);
	}

}
