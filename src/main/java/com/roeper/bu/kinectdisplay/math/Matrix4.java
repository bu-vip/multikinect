package edu.bu.vip.multikinectdisplay.math;

public class Matrix4
{
	public static final Matrix4 identity = new Matrix4( 1, 0, 0, 0,
														0, 1, 0, 0,
														0, 0, 1, 0,
														0, 0, 0, 1);
	
	//m<ROW><COL>
	public final float m00, m01, m02, m03;
	public final float m10, m11, m12, m13;
	public final float m20, m21, m22, m23;
	public final float m30, m31, m32, m33;
	public final float[] columnMajor;
	
	public Matrix4(float aM00, float aM01, float aM02, float aM03,
			float aM10, float aM11, float aM12, float aM13,
			float aM20, float aM21, float aM22, float aM23,
			float aM30, float aM31, float aM32, float aM33)
	{
		this.m00 = aM00; this.m01 = aM01; this.m02 = aM02; this.m03 = aM03;
		this.m10 = aM10; this.m11 = aM11; this.m12 = aM12; this.m13 = aM13;
		this.m20 = aM20; this.m21 = aM21; this.m22 = aM22; this.m23 = aM23;
		this.m30 = aM30; this.m31 = aM31; this.m32 = aM32; this.m33 = aM33;
		this.columnMajor = new float[16];
		this.columnMajor[0] = this.m00;
		this.columnMajor[1] = this.m10;
		this.columnMajor[2] = this.m20;
		this.columnMajor[3] = this.m30;
		this.columnMajor[4] = this.m01;
		this.columnMajor[5] = this.m11;
		this.columnMajor[6] = this.m21;
		this.columnMajor[7] = this.m31;
		this.columnMajor[8] = this.m02;
		this.columnMajor[9] = this.m12;
		this.columnMajor[10] = this.m22;
		this.columnMajor[11] = this.m32;
		this.columnMajor[12] = this.m03;
		this.columnMajor[13] = this.m13;
		this.columnMajor[14] = this.m23;
		this.columnMajor[15] = this.m33;
	}
	
	public Matrix4 add(Matrix4 aMat)
	{
		Matrix4 result = new Matrix4(this.m00 + aMat.m00, this.m01 + aMat.m01, this.m02 + aMat.m02, this.m03 + aMat.m03,
									 this.m10 + aMat.m10, this.m11 + aMat.m11, this.m12 + aMat.m12, this.m13 + aMat.m13,
									 this.m20 + aMat.m20, this.m21 + aMat.m21, this.m22 + aMat.m22, this.m23 + aMat.m23,
									 this.m30 + aMat.m30, this.m31 + aMat.m31, this.m32 + aMat.m32, this.m33 + aMat.m33);
		return result;
	}
	
	public Matrix4 subtract(Matrix4 aMat)
	{
		Matrix4 result = new Matrix4(this.m00 - aMat.m00, this.m01 - aMat.m01, this.m02 - aMat.m02, this.m03 - aMat.m03,
									 this.m10 - aMat.m10, this.m11 - aMat.m11, this.m12 - aMat.m12, this.m13 - aMat.m13,
									 this.m20 - aMat.m20, this.m21 - aMat.m21, this.m22 - aMat.m22, this.m23 - aMat.m23,
									 this.m30 - aMat.m30, this.m31 - aMat.m31, this.m32 - aMat.m32, this.m33 - aMat.m33);
		return result;
	}
	
	public Matrix4 scale(float aScale)
	{
		Matrix4 result = new Matrix4(this.m00 * aScale, this.m01 * aScale, this.m02 * aScale, this.m03 * aScale,
									 this.m10 * aScale, this.m11 * aScale, this.m12 * aScale, this.m13 * aScale,
									 this.m20 * aScale, this.m21 * aScale, this.m22 * aScale, this.m23 * aScale,
									 this.m30 * aScale, this.m31 * aScale, this.m32 * aScale, this.m33 * aScale);
		return result;
	}
	
	public Matrix4 multiply(Matrix4 aMat)
	{
		float m00 = this.m00 * aMat.m00 + this.m01 * aMat.m10 + this.m02 * aMat.m20 + this.m03 * aMat.m30;
		float m01 = this.m00 * aMat.m01 + this.m01 * aMat.m11 + this.m02 * aMat.m21 + this.m03 * aMat.m31;
		float m02 = this.m00 * aMat.m02 + this.m01 * aMat.m12 + this.m02 * aMat.m22 + this.m03 * aMat.m32;
		float m03 = this.m00 * aMat.m03 + this.m01 * aMat.m13 + this.m02 * aMat.m23 + this.m03 * aMat.m33;
		
		float m10 = this.m10 * aMat.m00 + this.m11 * aMat.m10 + this.m12 * aMat.m20 + this.m13 * aMat.m30;
		float m11 = this.m10 * aMat.m01 + this.m11 * aMat.m11 + this.m12 * aMat.m21 + this.m13 * aMat.m31;
		float m12 = this.m10 * aMat.m02 + this.m11 * aMat.m12 + this.m12 * aMat.m22 + this.m13 * aMat.m32;
		float m13 = this.m10 * aMat.m03 + this.m11 * aMat.m13 + this.m12 * aMat.m23 + this.m13 * aMat.m33;
		
		float m20 = this.m20 * aMat.m00 + this.m21 * aMat.m10 + this.m22 * aMat.m20 + this.m23 * aMat.m30;
		float m21 = this.m20 * aMat.m01 + this.m21 * aMat.m11 + this.m22 * aMat.m21 + this.m23 * aMat.m31;
		float m22 = this.m20 * aMat.m02 + this.m21 * aMat.m12 + this.m22 * aMat.m22 + this.m23 * aMat.m32;
		float m23 = this.m20 * aMat.m03 + this.m21 * aMat.m13 + this.m22 * aMat.m23 + this.m23 * aMat.m33;
		
		float m30 = this.m30 * aMat.m00 + this.m31 * aMat.m10 + this.m32 * aMat.m20 + this.m33 * aMat.m30;
		float m31 = this.m30 * aMat.m01 + this.m31 * aMat.m11 + this.m32 * aMat.m21 + this.m33 * aMat.m31;
		float m32 = this.m30 * aMat.m02 + this.m31 * aMat.m12 + this.m32 * aMat.m22 + this.m33 * aMat.m32;
		float m33 = this.m30 * aMat.m03 + this.m31 * aMat.m13 + this.m32 * aMat.m23 + this.m33 * aMat.m33;
		
		Matrix4 result = new Matrix4(m00, m01, m02, m03, 
									 m10, m11, m12, m13, 
									 m20, m21, m22, m23, 
									 m30, m31, m32, m33);
		return result;
	}
	
	public Vector3 multiply(Vector3 aV)
	{
		float x = this.m00 * aV.x + this.m01 * aV.y + this.m02 * aV.z + this.m03;
		float y = this.m10 * aV.x + this.m11 * aV.y + this.m12 * aV.z + this.m13;
		float z = this.m20 * aV.x + this.m21 * aV.y + this.m22 * aV.z + this.m23;
		float w = this.m30 * aV.x + this.m31 * aV.y + this.m32 * aV.z + this.m33;
		if (w == 0)
			return Vector3.zero;
		Vector3 result = new Vector3(x / w, y / w, z / w);
		return result;
	}
	
	public static Matrix4 translate(Vector3 aTranslate)
	{
		return Matrix4.translate(aTranslate.x, aTranslate.y, aTranslate.z);
	}
	
	public static Matrix4 translate(float aX, float aY, float aZ)
	{
		Matrix4 result = new Matrix4( 1, 0, 0, aX,
									  0, 1, 0, aY,
									  0, 0, 1, aZ,
									  0, 0, 0, 1);
		return result;
	}
	
	public static Matrix4 scale(Vector3 aScale)
	{
		return Matrix4.scale(aScale.x, aScale.y, aScale.z);
	}
	
	public static Matrix4 scale(float aX, float aY, float aZ)
	{
		Matrix4 result = new Matrix4( aX, 0, 0, 0,
									  0, aY, 0, 0,
									  0, 0, aZ, 0,
									  0, 0, 0, 1);
		return result;
	}
	
	public static Matrix4 lookAt(Vector3 aCamPos, Vector3 aTarget, Vector3 aUp)
	{
		// Formula taken from Microsoft docs 
		Vector3 zAxis = aCamPos.subtract(aTarget).normalize();
		Vector3 xAxis = aUp.crossProduct(zAxis).normalize();
		Vector3 yAxis = zAxis.crossProduct(xAxis);
		float dotX = xAxis.dotProduct(aCamPos);
		float dotY = yAxis.dotProduct(aCamPos);
		float dotZ = zAxis.dotProduct(aCamPos);
		
		Matrix4 result = new Matrix4(xAxis.x, yAxis.x, zAxis.x, 0,
									 xAxis.y, yAxis.y, zAxis.y, 0,
									 xAxis.z, yAxis.z, zAxis.z, 0,
									 -dotX, -dotY, -dotZ, 1);
		return result;	
	}
	
	public static Matrix4 frustum(float aLeft, float aRight, float aTop, float aBottom, float aNear, float aFar)
	{
		float m00 = 2 * aNear / (aRight - aLeft);
		float m11 = 2 * aNear / (aTop - aBottom);
		float m22 = - (aFar + aNear) / (aFar - aNear);
		float m02 = (aRight + aLeft) / (aRight - aLeft);
		float m12 = (aTop + aBottom) / (aTop - aBottom);
		float m23 = - (2 * aFar * aNear) / (aFar - aNear);
		
		Matrix4 result = new Matrix4(m00, 0, m02, 0,
									 0, m11, m12, 0,
									 0, 0, m22, m23,
									 0, 0, -1, 0);
		return result;
	}
	
	@Override
	public String toString()
	{
		String result = "{" + this.m00 + ", " + this.m01 + ", " + this.m02 + ", " + this.m03 + ", \n";
		result += this.m10 + ", " + this.m11 + ", " + this.m12 + ", " + this.m13 + ", \n";
		result += this.m20 + ", " + this.m21 + ", " + this.m22 + ", " + this.m23 + ", \n";
		result += this.m30 + ", " + this.m31 + ", " + this.m32 + ", " + this.m33 + "} \n";
		return result;
	}
}
