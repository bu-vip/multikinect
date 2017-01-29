package com.roeper.bu.kinectdisplay.math;

/** Vector of 2 floats.
 * 
 * @author Doug
 *
 */
public class Vector2
{
	public static final Vector2 zero = new Vector2(0, 0);
	public static final Vector2 one = new Vector2(1, 1);
	public static final Vector2 i = new Vector2(1, 0);
	public static final Vector2 j = new Vector2(0, 1);
	
	public final float x, y;
	
	public Vector2 (float aX, float aY)
	{
		x = aX;
		y = aY;
	}
	
	public Vector2 (double aX, double aY)
	{
		x = (float)aX;
		y = (float)aY;
	}
	
	public Vector2 add(Vector2 aV2)
	{
		return new Vector2(this.x + aV2.x, this.y + aV2.y);
	}
	
	public Vector2 subtract(Vector2 aV2)
	{
		return new Vector2(this.x - aV2.x, this.y - aV2.y);
	}
	
	public Vector2 scale(float aScale)
	{
		return new Vector2(this.x * aScale, this.y * aScale);
	}
	
	public Vector2 normalize()
	{
		float scale = (float)Math.sqrt(this.x * this.x + this.y * this.y);
		if (scale == 0.0f)
			return Vector2.zero;
		return scale(1.0f / scale);
	}
	
	public float dotProduct(Vector2 aV2)
	{
		float result = this.x * aV2.x + this.y * aV2.y;
		return result;
	}
	
	public float length()
	{
		float result = (float)Math.sqrt(this.x * this.x + this.y * this.y);
		return result;
	}
	
	@Override
	public String toString()
	{
		return "<" + this.x + ", " + this.y + ">";
	}
	
	public static Vector2 random()
	{
		return new Vector2(Math.random(), Math.random());
	}
}
