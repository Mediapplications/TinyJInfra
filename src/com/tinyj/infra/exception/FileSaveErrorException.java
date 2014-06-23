package com.tinyj.infra.exception;

@SuppressWarnings("serial")
public class FileSaveErrorException extends CodedException
{
	public FileSaveErrorException(String aMsg, int aErrorCode)
	{
		super(aMsg, aErrorCode);
	}

}
