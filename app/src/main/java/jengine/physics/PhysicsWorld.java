package jengine.physics;

import jengine.JEngine;
import jengine.objects.SimObject;
import jengine.objects.DynamicAtom;

import java.util.List;

public class PhysicsWorld {
  private float width;
  private float height;
  private Constraint border = null;
  private float[] gravity = new float[] {0f, 500f};
  private float damping = 0.9f;
  private Grid grid = new Grid(50f);

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

  public float[] centre() {
    return new float[] {width / 2, height / 2};
  }

  public boolean holds(SimObject o) {
    float x = o.position().x;
    float y = o.position().y;
    float r = o.boundary();
    return (x - r > width || x + r < 0 || y - r > height || y + r < 0);
  }

  public void setGravity(float[] gravity) {
    if (gravity.length != 2)
      throw new IllegalArgumentException("expected 2 components, got " + gravity.length);
    this.gravity = gravity;
  }

  public void setBorder(int type) {
    switch (type) {
      case JEngine.BORDER_NONE:
        this.border = null;
        break;
      case JEngine.BORDER_RECT:
        this.border = new RectangularBorder(width, height);
        break;
      case JEngine.BORDER_CIRCLE:
        this.border = new CirclularBorder(width / 2.5f, centre());
        break;
      default:
        throw new IllegalArgumentException("invalid border type");
    }
  }

  public void step(List<? extends SimObject> objects, float dt) {
    step(objects, dt, 1);
  }

  public void step(List<? extends SimObject> objects, float dt, int subSteps) {
    if (dt < 0 || subSteps <= 0)
      throw new IllegalArgumentException();
    float subdt = dt / (float) subSteps;
    for (int i = 0; i < subSteps; i++) {
      applyGravity(objects);
      updateObjects(objects, subdt);
      solveCollisionGrid(objects);
      applyConstraints(objects);
    }
  }

  private void applyGravity(List<? extends SimObject> objects) {
    for (SimObject x : objects) {
      if (x instanceof DynamicAtom atom && atom != null)
        atom.accelerate(gravity);
    }
  }

  private void updateObjects(List<? extends SimObject> objects, float dt) {
    for (SimObject x : objects) {
      if (x instanceof DynamicAtom atom && atom != null) {
        Vector vel = atom.velocity();
        atom.previousPosition().set(atom.position());
        // x1 = x0 + v + a * dt * dt
        atom.position().add(vel).add(Vector.scale(atom.acceleration(), dt * dt));
      }
    }
  }

  private void solveCollisionGrid(List<? extends SimObject> objects) {
    grid.rebuild(objects);
    grid.forEach((i, j) -> {
      SimObject o1 = objects.get(i);
      SimObject o2 = objects.get(j);
      resolveCollision(o1, o2);
    });
  }

  private boolean resolveCollision(SimObject o1, SimObject o2) {
    if (o1 == null || o2 == null || o1 == o2)
      return false;
    Vector delta = Vector.sub(o2.position(), o1.position());
    float distance = delta.magnitude();
    float overlap = (o1.boundary() + o2.boundary()) - distance;
    if (overlap > 0) {
      Vector correction = delta.normalise().scale(overlap / 2f);
      if (o1 instanceof DynamicAtom)
        o1.position().sub(correction);
      if (o2 instanceof DynamicAtom)
        o2.position().add(correction);
      return true;
    }
    return false;
  }

  private void applyConstraints(List<? extends SimObject> objects) {
    for (SimObject o : objects) {
      if (border != null) {
        border.applyConstraint(o);
      }
    }
  }

  private abstract class Constraint {
    abstract void applyConstraint(SimObject o);

    abstract boolean inBounds(SimObject o);

    abstract Vector centre();
  }

  private class RectangularBorder extends Constraint {
    private float width;
    private float height;

    RectangularBorder(float width, float height) {
      this.width = width;
      this.height = height;
    }

    boolean inBounds(SimObject o) {
      float x = o.position().x;
      float y = o.position().y;
      float r = o.boundary();
      return (x + r > width || x - r < 0 || y + r > height || y - r < 0);
    }

    Vector centre() {
      return new Vector(width / 2, height / 2);
    }

    void applyConstraint(SimObject o) {
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

  private class CirclularBorder extends Constraint {
    private float radius;
    private Vector centre;

    CirclularBorder(float radius, float[] centre) {
      if (centre.length != 2)
        throw new IllegalArgumentException();
      this.radius = radius;
      this.centre = new Vector(centre);
    }

    boolean inBounds(SimObject o) {
      Vector origin = Vector.sub(o.position(), centre);
      return (origin.magnitude() > radius - o.boundary());
    }

    Vector centre() {
      return centre;
    }

    void applyConstraint(SimObject o) {
      Vector origin = Vector.sub(o.position(), centre);
      float distance = origin.magnitude();
      if (distance > radius - o.boundary()) {
        origin.normalise();
        origin.scale(radius - o.boundary());
        o.position().set(Vector.add(centre, origin));
      }
    }
  }
}
