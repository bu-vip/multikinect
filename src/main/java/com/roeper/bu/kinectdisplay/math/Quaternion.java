package edu.bu.vip.multikinectdisplay.math;

/**
 * Quaternion for representing rotations.
 * 
 * @author Doug
 *
 */
public class Quaternion
{
	public static final Quaternion identity = new Quaternion(0, 0, 0, 1);

	public final float w;
	public final Vector3 v;

	public Quaternion(Vector3 aV, float aW)
	{
		this.w = aW;
		this.v = aV;
	}

	public Quaternion(Vector3 aV, double aW)
	{
		this.w = (float) aW;
		this.v = aV;
	}

	public Quaternion(float aX, float aY, float aZ, float aW)
	{
		this.w = aW;
		this.v = new Vector3(aX, aY, aZ);
	}

	public Quaternion normalize()
	{
		float scale = (float) Math.sqrt(this.w * this.w + this.v.x * this.v.x + this.v.y * this.v.y + this.v.z * this.v.z);
		return new Quaternion(this.v.scale(1 / scale), this.w / scale);
	}

	public Quaternion inverse()
	{
		return new Quaternion(this.v.scale(-1), this.w);
	}

	public Quaternion multiply(Quaternion aQ2)
	{
		float x = this.w * aQ2.v.x + this.v.x * aQ2.w + this.v.y * aQ2.v.z - this.v.z * aQ2.v.y;
		float y = this.w * aQ2.v.y + this.v.y * aQ2.w + this.v.z * aQ2.v.x - this.v.x * aQ2.v.z;
		float z = this.w * aQ2.v.z + this.v.z * aQ2.w + this.v.x * aQ2.v.y - this.v.y * aQ2.v.x;
		float w = this.w * aQ2.w - this.v.x * aQ2.v.x - this.v.y * aQ2.v.y - this.v.z * aQ2.v.z;
		return new Quaternion(x, y, z, w);
	}

	public Vector3 rotate(Vector3 aV)
	{
		Vector3 normalized = aV.normalize();
		Quaternion vectorQuat = new Quaternion(normalized, 0.0f);
		Quaternion result = this.multiply(vectorQuat).multiply(this.inverse());

		return result.v;
	}

	public float dotProduct(Quaternion aB)
	{
		float result = this.w * aB.w + this.v.dotProduct(aB.v);
		return result;
	}

	public Quaternion scale(float aScale)
	{
		return new Quaternion(this.v.scale(aScale), this.w * aScale);
	}

	public Quaternion add(Quaternion aB)
	{
		return new Quaternion(this.v.add(aB.v), this.w + aB.w);
	}

	public Matrix4 toMatrix()
	{
		// make sure quaternion is normalized
		Quaternion normalized = this.normalize();

		float x2 = normalized.v.x * normalized.v.x;
		float y2 = normalized.v.y * normalized.v.y;
		float z2 = normalized.v.z * normalized.v.z;
		float xy = normalized.v.x * normalized.v.y;
		float xz = normalized.v.x * normalized.v.z;
		float yz = normalized.v.y * normalized.v.z;
		float wx = normalized.w * normalized.v.x;
		float wy = normalized.w * normalized.v.y;
		float wz = normalized.w * normalized.v.z;

		Matrix4 matrix = new Matrix4( 1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy), 0.0f, 
									  2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx), 0.0f, 
									  2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2), 0.0f, 
									  0.0f, 0.0f, 0.0f, 1.0f );
		return matrix;
	}
	
	public boolean isValid()
	{
		return (!Float.isInfinite(this.w) && !Float.isNaN(this.w));
	}

	public static Quaternion fromAxisAngle(Vector3 aAxis, float aAngle)
	{
		float sinAngle = (float) Math.sin(aAngle * 0.5f);
		Vector3 normalized = aAxis.normalize();
		Vector3 v = normalized.scale(sinAngle);
		float w = (float) Math.cos(aAngle * 0.5f);
		return new Quaternion(v, w);
	}

	public static Quaternion fromEuler(float aRoll, float aPitch, float aYaw)
	{
		float sinp = (float) Math.sin(aPitch);
		float siny = (float) Math.sin(aYaw);
		float sinr = (float) Math.sin(aRoll);
		float cosp = (float) Math.cos(aPitch);
		float cosy = (float) Math.cos(aYaw);
		float cosr = (float) Math.cos(aRoll);

		float x = sinr * cosp * cosy - cosr * sinp * siny;
		float y = cosr * sinp * cosy + sinr * cosp * siny;
		float z = cosr * cosp * siny - sinr * sinp * cosy;
		float w = cosr * cosp * cosy + sinr * sinp * siny;

		Quaternion result = new Quaternion(x, y, z, w);
		return result.normalize();
	}

	public static Quaternion lerp(Quaternion aA, Quaternion aB, float aPercent)
	{
		//clamp to 0 -> 1
		aPercent = (aPercent > 1.0 ? 1.0f : aPercent);
		aPercent = (aPercent < 0.0 ? 0.0f : aPercent);
		float w = aA.w * (1 - aPercent) + aA.w * aPercent;
		Vector3 v = aA.v.scale(1 - aPercent).add(aB.v.scale(aPercent));
		Quaternion result = new Quaternion(v, w);
		return result;
	}
	
	@Override
	public String toString()
	{
		return "(" + this.w + ", " + this.v.toString() + ")";
	}
}
