/**
 * 
 */
package com.tinyj.infra.colors;

/**
 * A class representing a color from alpha, r, g, b
 * @author asaf.peeri
 *
 */
public class Color
{
	private byte mAlpha;
	private byte mRed;
	private byte mBlue;
	private byte mGreen;
	
	
	public Color(byte aAlpha, byte aRed, byte aGreen, byte aBlue)
	{
		setAlpha(aAlpha);
		setRed(aRed);
		setGreen(aGreen);
		setBlue(aBlue);
	}
	
	public Color()
	{
		
	}
	
	
	
	/**
	 * Converts the color to int value
	 * @return the int value representing the color (in hex: AARRGGBB)
	 */
	public int toInt()
	{
		int color = 0;
		color = mAlpha << 24;
		color = color | (mRed << 16);
		color = color | (mBlue << 8);
		color = color | mGreen;
		return color;
	}
	
	/**
	 * Creates color object from int representation
	 * @param aColor
	 * @return
	 */
	public static Color fromIntVal(int aColor)
	{
		Color color = new Color();
		color.fromInt(aColor);
		return color;
	}
	
	/**
	 * Create color from String hex representation (for example, from "FFFF0000")
	 * @param aHexVal
	 * @return
	 */
	public static Color fromStringVal(String aHexVal)
	{
		int val = ColorUtils.fromHexColor(aHexVal);
		return fromIntVal(val);
	}
	
	/**
	 * Sets fields to color from given int representation
	 * @param aColor
	 */
	public void fromInt(int aColor)
	{
		byte alpha = (byte)((0xFF000000 & aColor) >> 24);
		byte red = (byte)((0x00FF0000 & aColor) >> 16);
		byte green = (byte)((0x0000FF00 & aColor) >> 8);
		byte blue = (byte)((0x000000FF & aColor ));
		
		setAlpha(alpha);
		setRed(red);
		setGreen(green);
		setBlue(blue);


	}
	
	
	/**
	 * Returns hex representation of color
	 * @return
	 */
	public String toHexString()
	{
		
		return ColorUtils.toHexColor(toInt());
	}

	


	public void setAlpha(byte aAlpha) {
		this.mAlpha = aAlpha;
	}


	public byte getAlpha() {
		return mAlpha;
	}


	public void setRed(byte aRed) {
		this.mRed = aRed;
	}


	public byte getRed() {
		return mRed;
	}


	public void setBlue(byte aBlue) {
		this.mBlue = aBlue;
	}


	public byte getBlue() {
		return mBlue;
	}


	public void setGreen(byte mGreen) {
		this.mGreen = mGreen;
	}


	public byte getGreen() {
		return mGreen;
	}
	
	

}
