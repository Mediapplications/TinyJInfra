package com.tinyj.infra.exception;

@SuppressWarnings("serial")
public class ConfigurationException extends RuntimeException 
{
	public ConfigurationException(String aMsg)
	{
		super(aMsg);
	}
}
