package com.roeper.bu.kinectdisplay.graphics;

/** Represents a color in 0.0-1.0 components range.
 * 
 * @author Doug
 *
 */
public class Color3
{
	public static final Color3 white = new Color3(255, 255, 255);
	public static final Color3 black = new Color3(0, 0, 0);
	public static final Color3 red = new Color3(255, 0, 0);
	public static final Color3 orange = new Color3(255, 127, 0);
	public static final Color3 yellow = new Color3(255, 255, 0);
	public static final Color3 springGreen = new Color3(127, 255, 0);
	public static final Color3 green = new Color3(0, 255, 0);
	public static final Color3 turquoise = new Color3(0, 127, 0);
	public static final Color3 cyan = new Color3(0, 255, 255);
	public static final Color3 ocean = new Color3(0, 127, 255);
	public static final Color3 blue = new Color3(0, 0, 255);
	public static final Color3 violet = new Color3(127, 0, 255);
	public static final Color3 magenta = new Color3(255, 0, 255);
	public static final Color3 raspberry = new Color3(255, 0, 127);
	
	public final float r, g, b;
	
	public Color3 (int aRGB)
	{
		int ri = (aRGB & 0x00FF0000) >> 16;
		int gi = (aRGB & 0x0000FF00) >> 8;
		int bi = (aRGB & 0x000000FF) >> 0;
		r = ri / 255.0f;
		g = gi / 255.0f;
		b = bi / 255.0f;
	}
	
	public Color3 (int aRed, int aGreen, int aBlue)
	{
		r = aRed / 255.0f;
		g = aGreen / 255.0f;
		b = aBlue / 255.0f;
	}
	
	public Color3 (float aRed, float aGreen, float aBlue)
	{
		r = aRed;
		g = aGreen;
		b = aBlue;
	}
	
	public Color3 (double aRed, double aGreen, double aBlue)
	{
		r = (float)aRed;
		g = (float)aGreen;
		b = (float)aBlue;
	}
	
	public int getRGB()
	{
		int ri = Math.round(r * 255);
		int gi = Math.round(g * 255);
		int bi = Math.round(b * 255);

		return Color3.componentsToSingleInt(ri, gi, bi);
	}
	
	public Color3 add(Color3 aC2)
	{
		return new Color3(this.r + aC2.r, this.g + aC2.g, this.b + aC2.b);
	}
	
	public Color3 subtract(Color3 aC2)
	{
		return new Color3(this.r - aC2.r, this.g - aC2.g, this.b - aC2.b);
	}
	
	public Color3 scale(float aWeight)
	{
		return new Color3(this.r * aWeight, this.g * aWeight, this.b * aWeight);
	}
	
	public Color3 multiply(Color3 aColor)
	{
		return new Color3(this.r * aColor.r, this.g * aColor.g, this.b * aColor.b);
	}
	
	public Color3 clamp(float aMin, float aMax)
	{
		float r = Math.min(Math.max(this.r, aMin), aMax);
		float g = Math.min(Math.max(this.g, aMin), aMax);
		float b = Math.min(Math.max(this.b, aMin), aMax);
		return new Color3(r, g, b);
	}
	
	/** Class Methods
	 */
	
	public static int componentsToSingleInt(int aR, int aG, int aB)
	{
		int color = 0;
		color = (aR << 24) | (aG << 16) | (aB << 8) | 0xff;
		return color;
	}
	
	public static Color3 random()
	{
		return new Color3(Math.random(), Math.random(), Math.random());
	}

}
