package jengine.objects;

import jengine.physics.Vector;

public class DynamicAtom extends Atom {
  private Vector previousPosition;
  private Vector acceleration;

  public DynamicAtom(float mass, float radius, Float[] position) {
    super(mass, radius, position);
    this.previousPosition = this.position.clone();
  }

  public DynamicAtom(float mass, float radius, Float[] position, Float[] velocity) {
    super(mass, radius, position);
    if (velocity.length != 2) {
      throw new IllegalArgumentException("expected 2 velocity components, got " + velocity.length);
    }
    this.previousPosition = Vector.sub(this.position, new Vector(velocity));
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

  public void accelerate(Float[] components) {
    this.acceleration.set(components);
  }
}
