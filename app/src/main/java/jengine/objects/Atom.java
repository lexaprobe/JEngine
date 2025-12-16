package jengine.objects;

public abstract class Atom extends SimObject {
  public static final float RADIUS_MAX = 100f;
  public static final float RADIUS_MIN = 2f;
  public static final float RADIUS_DEFAULT = 30f;
  public static final float RADIUS_LARGE = 50f;
  public static final float RADIUS_SMALL = 5f;
  public static final float MASS_DEFAULT = 1f;

  protected float mass;
  protected float radius;

  public Atom(float[] position, float radius, float mass) {
    super(position);
    this.radius = radius;
    this.mass = mass;
  }

  public float mass() {
    return mass;
  }

  public float radius() {
    return radius;
  }

  @Override
  public float width() {
    return radius * 2;
  }

  @Override
  public float height() {
    return radius * 2;
  }

  @Override
  public float boundary() {
    return radius;
  }
}
