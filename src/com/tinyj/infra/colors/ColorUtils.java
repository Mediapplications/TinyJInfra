package com.tinyj.infra.colors;

public class ColorUtils 
{
	public static int fromHexColor(String aColor)
	{
		long color = Long.parseLong(aColor.toLowerCase(), 16);
		
		return (int)color;
	}
	
	
	public static String mergeAlpha(String aHexColor,String aAlpha)
	{
		byte alpha = Byte.parseByte(aAlpha.toLowerCase(),16);
		return mergeAlpha(aHexColor, alpha);
	}
	
	public static String mergeAlpha(int color,String aHexAlpha)
	{
		byte alpha = Byte.parseByte(aHexAlpha.toLowerCase(),16);
		int mergedColor =  mergeAlpha(color,alpha);
		return toHexColor(mergedColor);

	}
	
	
	public static String mergeAlpha(String aHexColor, byte aAlpha)
	{
		int color = fromHexColor(aHexColor);
		color = mergeAlpha(color,aAlpha);
		return toHexColor(color);
	}
	
	public static int mergeAlpha(int aColor, byte aAlpha)
	{
		int alpha = aAlpha;
		aColor = aColor & 0x00FFFFFF;
		alpha = alpha << 24;
		aColor = aColor | alpha;
		return aColor;
	}
	
	
	
	public static String toHexColor(int aColor)
	{
		return Integer.toHexString(aColor).toUpperCase();
	}
	
	
	
	public static String toHtmlHexColor(int aColor)
	{
		String hexColorStr = toHexColor(aColor); //Integer.toHexString(aColor).toUpperCase();
		//the first 2 digits are the alpha channel. in HTML we dont use it, so
		//here we remove it. also we add a prefix of "#" which is used by HTML colors.
		if ("0".equals(hexColorStr))
		{
			hexColorStr = "00000000";
		}
		
		hexColorStr = "#" + hexColorStr.substring(2, hexColorStr.length());
		return hexColorStr;
	}
	
	

	
	public static int fromHtmlHexColor(String aColor)
	{
		int integerColorVal = 0;
		String readyForServerColorStr = "";
		
		//first we need to remove the prefix "#", and add two hexa-decimal digits
		//for the alpha value (which should be always non-transparent value - FF)
		if (aColor != null && aColor.trim() != "")
		{
			if (aColor.startsWith("#"))
			{
				//remove the #
				readyForServerColorStr = aColor.substring(1);
			}
			
			//add the two alpha channel digits
			readyForServerColorStr = "FF" + readyForServerColorStr;
			
			//call the conversion method to get the color as integer
			integerColorVal = fromHexColor(readyForServerColorStr);
		}
		
		return integerColorVal;
	}
	
	
	public static void main(String[] args)
	{
		int color = 0xFF444444;
		byte b = (byte)255;
		System.out.println(b);
		System.out.println(color);
	}

}
