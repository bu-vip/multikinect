package com.roeper.bu.kinectdisplay.math;

/** Vector of 3 ints.
 * 
 * @author Doug
 *
 */
public class Vector3i
{
	public static final Vector3i zero = new Vector3i(0, 0, 0);
	public static final Vector3i one = new Vector3i(1, 1, 1);
	public static final Vector3i i = new Vector3i(1, 0, 0);
	public static final Vector3i j = new Vector3i(0, 1, 0);
	public static final Vector3i k = new Vector3i(0, 0, 1);
	
	public final int x, y, z;
	
	public Vector3i (int aX, int aY, int aZ)
	{
		x = aX;
		y = aY;
		z = aZ;
	}
	
	public Vector3i add(Vector3i aV2)
	{
		return new Vector3i(this.x + aV2.x, this.y + aV2.y, this.z + aV2.z);
	}
	
	public Vector3i subtract(Vector3i aV2)
	{
		return new Vector3i(this.x - aV2.x, this.y - aV2.y, this.z - aV2.z);
	}
	
	public Vector3i scale(int aScale)
	{
		return new Vector3i(this.x * aScale, this.y * aScale, this.z * aScale);
	}
	
	public float length()
	{
		int result = (int)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		return result;
	}
	
	@Override
	public String toString()
	{
		return "<" + this.x + ", " + this.y + ", " + this.z + ">";
	}
}
