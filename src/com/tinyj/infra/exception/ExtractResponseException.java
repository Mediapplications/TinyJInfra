package com.tinyj.infra.exception;

@SuppressWarnings("serial")
public class ExtractResponseException extends Exception
{

	public ExtractResponseException(String aMessage)
	{
		super(aMessage);
	}
	
	public ExtractResponseException(String aMessage, Throwable err)
	{
		super(aMessage, err);
	}


}
