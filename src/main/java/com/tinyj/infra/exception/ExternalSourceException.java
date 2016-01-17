/**
 * 
 */
package com.tinyj.infra.exception;

/**
 * This com.tinyj.infra.exception will be thrown when there is an com.tinyj.infra.exception
 * in the business logic of customers that have integration with our system
 * @author asaf.peeri
 *
 */
public class ExternalSourceException extends BaseException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3614043947482716046L;

	/**
	 * @param aMsg
	 */
	public ExternalSourceException(String aMsg)
	{
		super(aMsg);
		// TODO Auto-generated constructor stub
	}

}
