package jengine.objects;

import jengine.physics.Vector;

public abstract class Atom {
  protected float mass;
  protected float radius;
  protected Vector position;
  protected Integer[] colour = new Integer[] {255, 255, 255};

  public Atom(float mass, float radius, Float[] position) {
    if (position.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 positional components, got " + position.length);
    }
    this.mass = mass;
    this.radius = radius;
    this.position = new Vector(position);
  }

  public float mass() {
    return mass;
  }

  public float radius() {
    return radius;
  }

  public Vector position() {
    return position;
  }

  public Integer[] colour() {
    return colour;
  }

  public float distanceTo(Atom x) {
    if (x == null) {
      return 0f;
    }
    Vector p1 = this.position();
    Vector p2 = x.position();
    float xDiff = p2.x - p1.x;
    float yDiff = p2.y = p1.y;
    double distance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
    return (float) distance;
  }

  public void paint(Integer[] rgb) {
    if (rgb.length != 3) {
      throw new IllegalArgumentException("expected 3 values in RGB, got " + rgb.length);
    }
    for (int value : rgb) {
      value = value % 256;
    }
    this.colour = rgb;
  }

}
