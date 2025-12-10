package jengine.physics;

public class Vector {
  public float x;
  public float y;

  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector(Float[] components) {
    if (components.length != 2) {
      throw new IllegalArgumentException("expected 2 components, got " + components.length);
    }
    this.x = components[0];
    this.y = components[1];
  }

  public Vector() {
    this(0f, 0f);
  }

  /**
   * Compute the addition between two Vector objects. Equivalent to <code>C = A + B</code>.
   * 
   * @param v1 <code>A</code>
   * @param v2 <code>B</code>
   * @return the resultant Vector (<code>C</code>)
   */
  public static Vector add(Vector v1, Vector v2) {
    float x = v1.x + v2.x;
    float y = v1.y + v2.y;
    return new Vector(x, y);
  }

  /**
   * Subtract one Vector object from another. Equivalent to <code>C = A + (-B)</code>.
   * 
   * @param v1 <code>A</code>
   * @param v2 <code>B</code>
   * @return the resultant Vector (<code>C</code>)
   */
  public static Vector sub(Vector v1, Vector v2) {
    float x = v1.x - v2.x;
    float y = v1.y - v2.y;
    return new Vector(x, y);
  }

  /**
   * Compute the normal of a given Vector object.
   * 
   * @param vector a Vector object
   * @return the normal Vector
   */
  public static Vector normal(Vector vector) {
    float length = vector.magnitude();
    if (length == 0) {
      return vector.clone();
    }
    float x = vector.x / length;
    float y = vector.y / length;
    return new Vector(x, y);
  }

  public Float[] components() {
    return new Float[] {x, y};
  }

  /**
   * Compute the magnitude (length) of this Vector.
   * 
   * @return the magnitude
   */
  public float magnitude() {
    return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  public void set(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public void set(Float[] components) {
    if (components.length != 2) {
      throw new IllegalArgumentException("expected 2 components, got " + components.length);
    }
    this.x = components[0];
    this.y = components[1];
  }

  /**
   * Scale both components of this Vector by some number.
   * 
   * @param scalar some number
   * @return this Vector instance
   */
  public Vector scale(double scalar) {
    this.x = (float) (x * scalar);
    this.y = (float) (y * scalar);
    return this;
  }

  /**
   * Returns a new Vector instance with components equivalent to scaled values of this Vector's
   * components. Does <em>not</em> alter the callee Vector's components.
   * 
   * @param scalar some number
   * @return a new Vector instance
   */
  public Vector scaled(double scalar) {
    float x = (float) (this.x * scalar);
    float y = (float) (this.y * scalar);
    return new Vector(x, y);
  }

  /**
   * Add this Vector to another Vector. Equivalent to <code>A = A + B</code>.
   * 
   * @param vector a Vector object
   * @return this Vector instance
   */
  public Vector add(Vector vector) {
    if (vector == null) {
      return this;
    }
    Float[] components = vector.components();
    if (components.length != 2) {
      throw new RuntimeException("vector addition with invalid Vector2D object");
    }
    this.x += components[0];
    this.y += components[1];
    return this;
  }

  /**
   * Subtract a Vector from this Vector. Equivalent to <code>A = A + (-B)</code>.
   * 
   * @param vector a Vector object
   * @return this Vector instance
   */
  public Vector sub(Vector vector) {
    if (vector == null) {
      return this;
    }
    Float[] components = vector.components();
    if (components.length != 2) {
      throw new RuntimeException("vector subtraction with invalid Vector2D object");
    }
    this.x -= components[0];
    this.y -= components[1];
    return this;
  }

  /**
   * Compute the dot product of this and another Vector.
   * 
   * @param vector a Vector object
   * @return the resultant dot product
   */
  public float dot(Vector vector) {
    if (vector == null) {
      return x + y;
    }
    Float[] components = vector.components();
    if (components.length != 2) {
      throw new RuntimeException("dot product with invalid Vector2D object");
    }
    float x2 = components[0];
    float y2 = components[1];
    return x * x2 + y * y2;
  }

  /**
   * Noramlise this Vector. Equivalent to <code>A = A.scale(1 / |A|)</code>
   * 
   * @return this Vector
   */
  public Vector normalise() {
    float length = magnitude();
    if (length != 0) {
      this.set(x / length, y / length);
    }
    return this;
  }

  /**
   * Compute the normal of this Vector and return it. Equivalent to
   * <code>N = A.scaled(1 / |A|)</code>
   * 
   * @return the normal
   */
  public Vector normalised() {
    float length = magnitude();
    if (length == 0) {
      return this;
    }
    return new Vector(x / length, y / length);
  }

  @Override
  public Vector clone() {
    return new Vector(this.x, this.y);
  }
}
