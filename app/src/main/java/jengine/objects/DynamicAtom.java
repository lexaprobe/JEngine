package jengine.objects;

import jengine.physics.Vector;

public class DynamicAtom extends Atom {
  private Vector previousPosition;
  private Vector acceleration = new Vector();

  public DynamicAtom(float mass, float radius, float[] position) {
    super(mass, radius, position);
    previousPosition = this.position.clone();
  }

  public DynamicAtom(float mass, float radius, float[] position, float[] velocity) {
    super(mass, radius, position);
    if (velocity.length != 2) {
      throw new IllegalArgumentException("expected 2 velocity components, got " + velocity.length);
    }
    previousPosition = Vector.sub(this.position, new Vector(velocity));
  }

  public Vector previousPosition() {
    return previousPosition;
  }

  public Vector acceleration() {
    return acceleration;
  }

  public Vector velocity() {
    return Vector.sub(position, previousPosition);
  }

  public void accelerate(float[] components) {
    acceleration.set(components);
  }

  public void accelerate(Vector v) {
    if (v != null) {
      acceleration.set(v);
    }
  }
}
