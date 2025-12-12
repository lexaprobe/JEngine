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
   * Compute the addition between two Vector objects. Equivalent to {@code C = A + B}.
   * 
   * @param v1 {@code A}
   * @param v2 {@code B}
   * @return the resultant Vector ({@code C})
   */
  public static Vector add(Vector v1, Vector v2) {
    float x = v1.x + v2.x;
    float y = v1.y + v2.y;
    return new Vector(x, y);
  }

  /**
   * Subtract one Vector object from another. Equivalent to {@code C = A + (-B)}.
   * 
   * @param v1 {@code A}
   * @param v2 {@code B}
   * @return the resultant Vector ({@code C})
   */
  public static Vector sub(Vector v1, Vector v2) {
    float x = v1.x - v2.x;
    float y = v1.y - v2.y;
    return new Vector(x, y);
  }

  /**
   * Compute the scaled equivalent of a given Vector object. Equivalent to {@code B = A * s}
   * 
   * @param v {@code A}
   * @param scalar {@code s}
   * @return the scaled Vector ({@code B})
   */
  public static Vector scale(Vector v, double scalar) {
    float x = (float) (v.x * scalar);
    float y = (float) (v.y * scalar);
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

  /**
   * Set the components of this Vector to some value.
   * 
   * @param x
   * @param y
   */
  public void set(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Set the components of this Vector to some value.
   * 
   * @param components an array of two floats (x, y)
   */
  public void set(Float[] components) {
    if (components.length != 2) {
      throw new IllegalArgumentException("expected 2 components, got " + components.length);
    }
    this.x = components[0];
    this.y = components[1];
  }

  /**
   * Set this Vector equal to some other Vector.
   * 
   * @param v a Vector object
   */
  public void set(Vector v) {
    if (v != null) {
      this.x = v.x;
      this.y = v.y;
    }
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
   * Add this Vector to another Vector. Equivalent to {@code A = A + B}.
   * 
   * @param vector {@code B}
   * @return this Vector instance ({@code A})
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
   * Subtract a Vector from this Vector. Equivalent to {@code A = A + (-B)}.
   * 
   * @param vector {@code B}
   * @return this Vector instance ({@code A})
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
   * Noramlise this Vector. Equivalent to {@code A = A.scale(1 / A.magnitude())}
   * 
   * @return this Vector ({@code A})
   */
  public Vector normalise() {
    float length = magnitude();
    if (length != 0) {
      this.set(x / length, y / length);
    }
    return this;
  }

  @Override
  public Vector clone() {
    return new Vector(this.x, this.y);
  }
}
