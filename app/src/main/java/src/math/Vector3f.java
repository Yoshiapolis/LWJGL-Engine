package math;

public class Vector3f {
	
	private float x;
	private float y;
	private float z;
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f(Vector4f other) {
		this.x = other.getX();
		this.y = other.getY();
		this.z = other.getZ();
	}
	
	public Vector3f(Vector3f other) {
		this.x = other.getX();
		this.y = other.getY();
		this.z = other.getZ();
	}
	
	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public void negate() {
		this.x = -x;
		this.y = -y;
		this.z = -z;
	}
	
	public Vector3f copyOf() {
		return new Vector3f(this.x, this.y, this.z);
	}
	
	public float dotProduct(Vector3f other) {
		return this.getX()*other.getX() + this.getY()*other.getY() + this.getZ() * other.getZ();
	}
	
	public void multiplyScalar(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
	}
	
	public float magnitude() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalize() {
		this.multiplyScalar(1f/this.magnitude());
	}
	
	public void subtract(Vector3f other) {
		this.x -= other.getX();
		this.y -= other.getY();
		this.z -= other.getZ();
	}
	
	public void add(Vector3f other) {
		this.x += other.getX();
		this.y += other.getY();
		this.z += other.getZ();
	}
	
	public void multiply(Vector3f other) {
		this.x *= other.getX();
		this.y *= other.getY();
		this.z *= other.getZ();
	}
	
	public static Vector3f cross(Vector3f a, Vector3f b) {
		float x = a.getY()*b.getZ() - a.getZ()*b.getY();
		float y = a.getZ()*b.getX() - a.getX()*b.getZ();
		float z = a.getX()*b.getY() - a.getY()*b.getX();
		
		return new Vector3f(x, y, z);
	}
	
	public static Vector3f scale(Vector3f a, float b) {
		Vector3f retVal = a.copyOf();
		retVal.multiplyScalar(b);
		return retVal;
	}
	
	public static Vector3f add(Vector3f a, Vector3f b) {
		Vector3f retVal = a.copyOf();
		retVal.add(b);
		return retVal;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if(o == this) return true;
		
		if(!(o instanceof Vector3f)) return false;
		Vector3f other = (Vector3f) o;
		if(near(x, other.getX(), 0.001f) && near(y,other.getY(), 0.001f) && near(z,other.getZ(), 0.001f)) return true;
		return false;
		
	}
	
	private boolean near(float a, float b, float bias) {
		return (a < b + bias && a > b - bias);
	}
	 
	public void addX(float dx) {
		this.x += dx;
	}
	
	public void addY(float dy) {
		this.y += dy;
	}
	
	public void addZ(float dz) {
		this.z += dz;
	}

	public void subtractX(float dx) {
		this.x -= dx;
	}
	
	public void subtractY(float dy) {
		this.y -= dy;
	}
	
	public void subtractZ(float dz) {
		this.z -= dz;
	}
	
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	 
	public float[] getFloats() {
		return new float[] {this.x, this.y, this.z};
	}
}
