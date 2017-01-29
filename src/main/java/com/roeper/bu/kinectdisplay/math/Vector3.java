package com.roeper.bu.kinectdisplay.math;

/** Vector of 3 floats.
 * 
 * @author Doug
 *
 */
public class Vector3
{
	public static final Vector3 zero = new Vector3(0, 0, 0);
	public static final Vector3 one = new Vector3(1, 1, 1);
	public static final Vector3 i = new Vector3(1, 0, 0);
	public static final Vector3 j = new Vector3(0, 1, 0);
	public static final Vector3 k = new Vector3(0, 0, 1);
	
	public final float x, y, z;
	
	public Vector3 (float aX, float aY, float aZ)
	{
		x = aX;
		y = aY;
		z = aZ;
	}
	
	public Vector3 (double aX, double aY, double aZ)
	{
		x = (float)aX;
		y = (float)aY;
		z = (float)aZ;
	}
	
	public Vector3 add(Vector3 aV2)
	{
		return new Vector3(this.x + aV2.x, this.y + aV2.y, this.z + aV2.z);
	}
	
	public Vector3 subtract(Vector3 aV2)
	{
		return new Vector3(this.x - aV2.x, this.y - aV2.y, this.z - aV2.z);
	}
	
	public Vector3 scale(float aScale)
	{
		return new Vector3(this.x * aScale, this.y * aScale, this.z * aScale);
	}
	
	public Vector3 normalize()
	{
		float scale = (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		if (scale == 0.0f)
			return Vector3.zero;
		return scale(1.0f / scale);
	}
	
	public float dotProduct(Vector3 aV2)
	{
		float result = this.x * aV2.x + this.y * aV2.y + this.z * aV2.z;
		return result;
	}
	
	public Vector3 crossProduct(Vector3 aV2)
	{
		float x = this.y * aV2.z - this.z * aV2.y;
		float y = -1 * (this.x * aV2.z - this.z * aV2.x);
		float z = this.x * aV2.y - this.y * aV2.x;
		return new Vector3(x, y, z);
	}
	
	public float length()
	{
		float result = (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		return result;
	}
	
	public Vector3 reflect(Vector3 aNormal)
	{
		float mag = 2 * this.dotProduct(aNormal);
		Vector3 result = aNormal.scale(mag).subtract(this);
		return result;
	}
	
	@Override
	public String toString()
	{
		return "<" + this.x + ", " + this.y + ", " + this.z + ">";
	}
	
	public static Vector3 random()
	{
		return new Vector3(Math.random(), Math.random(), Math.random());
	}
}
