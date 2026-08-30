package jengine.objects;

import jengine.physics.Vector;

public class Atom extends VerletObject {
  public static final float RADIUS_MAX = 3f;
  public static final float RADIUS_MIN = 0.06f;
  public static final float RADIUS_DEFAULT = 1f;
  public static final float RADIUS_LARGE = 1.5f;
  public static final float RADIUS_SMALL = 0.15f;

  private float radius = RADIUS_DEFAULT;

  public Atom(Vector position, Vector velocity, float mass, float radius, int[] colour) {
    super(position, velocity, mass, colour);
    this.radius = radius;
  }

  public Atom(Vector position, Vector velocity, float mass, float radius) {
    this(position, velocity, mass, radius, VerletObject.COLOUR_DEFAULT);
  }

  public Atom(Vector position, float mass, float radius) {
    this(position, new Vector(), mass, radius, VerletObject.COLOUR_DEFAULT);
  }

  public float radius() {
    return radius;
  }

  public void setRadius(float radius) {
    if (radius < 0) {
      throw new IllegalArgumentException("negative radius");
    }
    this.radius = radius;
  }

  @Override
  public float minX() {
    return position.x - radius;
  }

  @Override
  public float minY() {
    return position.y - radius;
  }

  @Override
  public float maxX() {
    return position.x + radius;
  }

  @Override
  public float maxY() {
    return position.y + radius;
  }

  @Override
  public float boundary() {
    return radius;
  }

  @Override
  public float width() {
    return radius * 2f;
  }

  @Override
  public float height() {
    return radius * 2f;
  }
}
