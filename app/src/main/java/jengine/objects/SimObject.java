package jengine.objects;

import jengine.physics.Vector;
import jengine.gfx.Renderer;

public abstract class SimObject {
  protected Vector position;
  protected float width;
  protected float height;
  protected int[] colour = Renderer.WHITE;

  public abstract float minX();

  public abstract float minY();

  public abstract float maxX();

  public abstract float maxY();

  public SimObject(float[] position) {
    if (position.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 positional components, got " + position.length);
    }
    this.position = new Vector(position);
  }

  public Vector position() {
    return position;
  }

  public int[] colour() {
    return colour;
  }

  public float width() {
    return width;
  }

  public float height() {
    return height;
  }

  public float boundary() {
    return width;
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

  public float distanceTo(SimObject x) {
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
}
