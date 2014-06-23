/**
 * 
 */
package com.tinyj.infra.exception;


/**
 * @author asaf.peeri
 *
 */
public class HTTPStatusException extends BaseException
{
	
	
	private int mStatus;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 0L;

	/**
	 * @param aMsg
	 * @param aResponseCode 
	 */
	public HTTPStatusException(String aMsg, int aResponseCode)
	{
		super(aMsg);
		mStatus = aResponseCode;
	}

	public void setStatus(int status)
	{
		mStatus = status;
	}

	public int getStatus()
	{
		return mStatus;
	}
	
	

}
