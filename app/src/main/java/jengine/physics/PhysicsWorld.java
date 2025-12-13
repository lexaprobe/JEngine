package jengine.physics;

import jengine.objects.Atom;
import jengine.objects.DynamicAtom;
import java.util.List;
import java.util.ArrayList;

public class PhysicsWorld {
  public static final int RECT = 1;
  public static final int CIRC = 2;

  private float width;
  private float height;
  private List<Atom> objects = new ArrayList<>();
  private float[] gravity = new float[] {0f, 300f};
  private float damping = 0.9f;

  public PhysicsWorld(float width, float height) {
    this.width = width;
    this.height = height;
  }

  public PhysicsWorld(float[] size) {
    if (size.length != 2)
      throw new IllegalArgumentException();
    width = size[0];
    height = size[1];
  }

  public PhysicsWorld(int[] size) {
    if (size.length != 2)
      throw new IllegalArgumentException();
    width = (float) size[0];
    height = (float) size[1];
  }

  public float width() {
    return width;
  }

  public float height() {
    return height;
  }

  public boolean addObject(Atom atom) {
    if (atom == null)
      return false;
    objects.add(atom);
    return true;
  }

  public boolean removeObject(Atom atom) {
    if (objects.contains(atom)) {
      objects.remove(atom);
      return true;
    }
    return false;
  }

  public List<Atom> getObjects() {
    return objects;
  }

  public void clear() {
    objects.clear();
  }

  public float[] centre() {
    return new float[] {width / 2, height / 2};
  }

  public void setGravity(float[] gravity) {
    if (gravity.length != 2)
      throw new IllegalArgumentException("expected 2 components, got " + gravity.length);
    this.gravity = gravity;
  }

  public void step(float dt) {
    step(dt, 1);
  }

  public void step(float dt, int subSteps) {
    if (dt < 0 || subSteps <= 0)
      throw new IllegalArgumentException();
    float subdt = dt / (float) subSteps;
    for (int i = 0; i < subSteps; i++) {
      applyGravity();
      updateObjects(subdt);
      resolveCollisions();
      applyConstraints();
    }
  }

  private void applyGravity() {
    for (Atom x : objects) {
      if (x instanceof DynamicAtom atom && atom != null)
        atom.accelerate(gravity);
    }
  }

  private void updateObjects(float dt) {
    for (Atom x : objects) {
      if (x instanceof DynamicAtom atom && atom != null) {
        Vector vel = atom.velocity();
        atom.previousPosition().set(atom.position());
        // x1 = x0 + v + a * dt * dt
        atom.position().add(vel).add(Vector.scale(atom.acceleration(), dt * dt));
      }
    }
  }

  private void resolveCollisions() {
    for (Atom a1 : objects) {
      for (Atom a2 : objects) {
        if (a1 == a2)
          continue;
        Vector delta = Vector.sub(a2.position(), a1.position());
        float distance = delta.magnitude();
        float overlap = (a1.radius() + a2.radius()) - distance;
        if (overlap > 0) {
          Vector correction = delta.normalise().scale(overlap / 2f);
          if (a1 instanceof DynamicAtom)
            a1.position().sub(correction);
          if (a2 instanceof DynamicAtom)
            a2.position().add(correction);
        }
      }
    }
  }

  private void applyConstraints() {
    for (Atom o : objects) {
      if (o instanceof DynamicAtom atom && atom != null) {
        float x = atom.position().x;
        float y = atom.position().y;
        float r = atom.radius();
        if (x + r > width) {
          Vector vel = atom.velocity();
          atom.position().set(width - r, y);
          vel.x *= -damping;
          atom.previousPosition().set(Vector.sub(atom.position(), vel));
        } else if (x - r < 0) {
          Vector vel = atom.velocity();
          atom.position().set(r, y);
          vel.x *= -damping;
          atom.previousPosition().set(Vector.sub(atom.position(), vel));
        } else if (y + r > height) {
          Vector vel = atom.velocity();
          atom.position().set(x, height - r);
          vel.y *= -damping;
          atom.previousPosition().set(Vector.sub(atom.position(), vel));
        } else if (y - r < 0) {
          Vector vel = atom.velocity();
          atom.position().set(x, r);
          vel.y *= -damping;
          atom.previousPosition().set(Vector.sub(atom.position(), vel));
        }
      }
    }
  }
}
