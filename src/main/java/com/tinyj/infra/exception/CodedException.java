package com.tinyj.infra.exception;

@SuppressWarnings("serial")
public class CodedException extends BaseException
{
	public int mErrorCode;
	
	public CodedException(String aMsg, int aErrorCode)
	{
		super(aMsg);
		mErrorCode = aErrorCode;
	}
}
