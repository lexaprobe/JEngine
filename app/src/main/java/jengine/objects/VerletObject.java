package jengine.objects;

import jengine.physics.Vector;

public abstract class VerletObject {
  public static final float SIZE_MIN = 0.01f; // 1 cm
  public static final float MASS_DEFAULT = 1f;
  public static final int[] COLOUR_DEFAULT = new int[] {0, 0, 0};

  protected Vector position;
  protected Vector previousPosition;
  protected Vector acceleration;
  protected float mass;
  protected int[] colour;

  public VerletObject(Vector position, Vector velocity, Vector acceleration, float mass,
      int[] colour) {
    this.position = position;
    this.previousPosition = Vector.sub(position, velocity);
    this.acceleration = acceleration;
    this.mass = mass;
    if (colour.length != 3) {
      throw new IllegalArgumentException("expected 3 values in RGB, got " + colour.length);
    }
    this.colour = colour;
  }

  public VerletObject(Vector position, Vector velocity, float mass, int[] colour) {
    this(position, velocity, new Vector(), mass, colour);
  }

  public VerletObject(Vector position, Vector velocity, float mass) {
    this(position, velocity, new Vector(), mass, COLOUR_DEFAULT);
  }

  public VerletObject(Vector position, float mass) {
    this(position, new Vector(), mass, COLOUR_DEFAULT);
  }

  public VerletObject(Vector position, int[] colour) {
    this(position, new Vector(), MASS_DEFAULT, colour);
  }

  public VerletObject(Vector position) {
    this(position, COLOUR_DEFAULT);
  }

  public VerletObject(float[] position) {
    this(new Vector(position));
  }

  public abstract float width();

  public abstract float height();

  public abstract float minX();

  public abstract float minY();

  public abstract float maxX();

  public abstract float maxY();

  public abstract float boundary();

  public Vector position() {
    return position;
  }

  public Vector previousPosition() {
    return previousPosition;
  }

  public Vector velocity() {
    return Vector.sub(position, previousPosition);
  }

  public Vector acceleration() {
    return acceleration;
  }

  public float mass() {
    return mass;
  }

  public int[] colour() {
    return colour;
  }

  public void accelerate(Vector vector) {
    if (vector != null) {
      acceleration.set(vector);
    }
  }

  public void setMass(float mass) {
    if (mass < 0) {
      throw new IllegalArgumentException("negative mass");
    }
    this.mass = mass;
  }

  public void paint(int[] rgb) {
    if (rgb.length != 3) {
      throw new IllegalArgumentException("expected 3 values in RGB, got " + rgb.length);
    }
    for (int value : rgb) {
      value = value % 256;
    }
    this.colour = rgb;
  }

  public float distanceTo(VerletObject object) {
    if (object == null) {
      return 0f;
    }
    return this.position().distanceTo(object.position());
  }
}
