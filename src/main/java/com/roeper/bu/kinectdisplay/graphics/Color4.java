package edu.bu.vip.multikinectdisplay.graphics;

/** Represents a color in 0.0-1.0 components range.
 * 
 * @author Doug
 *
 */
public class Color4
{
	public static final Color4 trans = new Color4(0, 0, 0, 0);
	public static final Color4 white = new Color4(255, 255, 255, 255);
	public static final Color4 black = new Color4(0, 0, 0, 255);
	public static final Color4 red = new Color4(255, 0, 0, 255);
	public static final Color4 orange = new Color4(255, 127, 0, 255);
	public static final Color4 yellow = new Color4(255, 255, 0, 255);
	public static final Color4 springGreen = new Color4(127, 255, 0, 255);
	public static final Color4 green = new Color4(0, 255, 0, 255);
	public static final Color4 turquoise = new Color4(0, 127, 0, 255);
	public static final Color4 cyan = new Color4(0, 255, 255, 255);
	public static final Color4 ocean = new Color4(0, 127, 255, 255);
	public static final Color4 blue = new Color4(0, 0, 255, 255);
	public static final Color4 violet = new Color4(127, 0, 255, 255);
	public static final Color4 magenta = new Color4(255, 0, 255, 255);
	public static final Color4 raspberry = new Color4(255, 0, 127, 255);
	
	public final float r, g, b, a;
	
	public Color4 (int aRGBA)
	{
		int ri = (aRGBA & 0xFF000000) >> 24;
		int gi = (aRGBA & 0xFF0000) >> 16;
		int bi = (aRGBA & 0xFF00) >> 8;
		int ai = (aRGBA & 0xFF);
		r = ri / 255.0f;
		g = gi / 255.0f;
		b = bi / 255.0f;
		a = ai / 255.0f;
	}
	
	public Color4 (int aRed, int aGreen, int aBlue, int aAlpha)
	{
		r = aRed / 255.0f;
		g = aGreen / 255.0f;
		b = aBlue / 255.0f;
		a = aAlpha / 255.0f;
	}
	
	public Color4 (float aRed, float aGreen, float aBlue, float aAlpha)
	{
		r = aRed;
		g = aGreen;
		b = aBlue;
		a = aAlpha;
	}
	
	public Color4 (double aRed, double aGreen, double aBlue, double aAlpha)
	{
		r = (float)aRed;
		g = (float)aGreen;
		b = (float)aBlue;
		a = (float)aAlpha;
	}
	
	public int getRGB()
	{
		int ri = Math.round(r * 255);
		int gi = Math.round(g * 255);
		int bi = Math.round(b * 255);
		int ai = Math.round(a * 255);

		return Color4.componentsToSingleInt(ri, gi, bi, ai);
	}
	
	public Color3 rgb()
	{
		return new Color3(this.r, this.g, this.b);
	}
	
	public Color4 add(Color4 aC2)
	{
		return new Color4(this.r + aC2.r, this.g + aC2.g, this.b + aC2.b, this.a + aC2.a);
	}
	
	public Color4 scale(float aWeight)
	{
		return new Color4(this.r * aWeight, this.g * aWeight, this.b * aWeight, this.a * aWeight);
	}
	
	/** Class Methods
	 */
	
	public static int componentsToSingleInt(int aR, int aG, int aB, int aA)
	{
		int color = 0;
		color = (aR << 24) | (aG << 16) | (aB << 8) | aA;
		return color;
	}
	
	public static Color4 random()
	{
		return new Color4(Math.random(), Math.random(), Math.random(), Math.random());
	}

}
