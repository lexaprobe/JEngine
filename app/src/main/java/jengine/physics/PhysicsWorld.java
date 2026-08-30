package jengine.physics;

import jengine.objects.VerletObject;
import java.util.List;

public class PhysicsWorld {
  public static final int GRAVITY_NONE = 0;
  public static final int GRAVITY_UNIFORM = 1;
  public static final int GRAVITY_POINT = 2;
  public static final int GRAVITY_DEFAULT = GRAVITY_NONE;

  private final float[] centre;
  private final Constraint constraint;

  private Vector gravity = new Vector();
  private int gravityMode = GRAVITY_DEFAULT;
  private float gravityStrength = 1000f;
  private float damping = 0.9f;
  private Grid collisionGrid = new Grid(50f);

  public PhysicsWorld(float[] centre) {
    if (centre.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 values for a centre (x, y), got " + centre.length);
    }
    this.centre = centre;
    constraint = null;
  }

  public PhysicsWorld(float[] centre, float width, float height) {
    if (centre.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 values for a centre (x, y), got " + centre.length);
    }
    this.centre = centre;
    constraint = new RectangularBorder(width, height);
  }

  public PhysicsWorld(float[] centre, float radius) {
    if (centre.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 values for a centre (x, y), got " + centre.length);
    }
    this.centre = centre;
    if (radius < 0) {
      throw new IllegalArgumentException("negative radius: " + radius);
    }
    constraint = new CirclularBorder(radius, centre);
  }

  public float[] centre() {
    return centre;
  }

  public void setGravity(Vector gravity) {
    this.gravity = gravity;
  }

  public boolean contains(VerletObject o) {
    if (constraint != null) {
      return constraint.contains(o);
    }
    // no world border, so yes
    return true;
  }

  public void setGravityMode(int mode) {
    gravityMode = mode;
  }

  public void setGravityMultiplier(float multiplier) {
    gravityStrength *= multiplier;
  }

  public void step(List<? extends VerletObject> objects, double dt, int subSteps) {
    if (dt < 0 || subSteps <= 0)
      throw new IllegalArgumentException();
    double subdt = dt / (double) subSteps;
    for (int i = 0; i < subSteps; i++) {
      switch (gravityMode) {
        case GRAVITY_POINT:
          applyPointGravity(objects);
          break;
        default:
          applyUniformGravity(objects);
          break;
      }
      updateObjects(objects, subdt);
      solveCollisionGrid(objects);
      applyConstraints(objects);
    }
  }

  private void applyUniformGravity(List<? extends VerletObject> objects) {
    for (VerletObject o : objects) {
      if (o == null)
        continue;
      o.accelerate(gravity);
    }
  }

  private void applyPointGravity(List<? extends VerletObject> objects) {
    Vector target = gravity;
    for (VerletObject o : objects) {
      if (o == null)
        continue;
      Vector direction = Vector.sub(target, o.position());
      float distance = direction.magnitude();
      if (distance > 0.0001f) {
        direction.normalise();
        o.accelerate(direction.scale(gravityStrength / Math.max(distance, VerletObject.SIZE_MIN)));
      }
    }
  }

  private void updateObjects(List<? extends VerletObject> objects, double dt) {
    for (VerletObject o : objects) {
      if (o == null)
        continue;
      Vector vel = o.velocity();
      o.previousPosition().set(o.position());
      // x1 = x0 + v + a * dt * dt
      o.position().add(vel).add(Vector.scale(o.acceleration(), dt * dt));
    }
  }

  private void solveCollisionGrid(List<? extends VerletObject> objects) {
    collisionGrid.rebuild(objects);
    collisionGrid.forEach((i, j) -> {
      VerletObject o1 = objects.get(i);
      VerletObject o2 = objects.get(j);
      resolveCollision(o1, o2);
    });
  }

  private boolean resolveCollision(VerletObject o1, VerletObject o2) {
    if (o1 == null || o2 == null || o1 == o2)
      return false;
    Vector delta = Vector.sub(o2.position(), o1.position());
    float distance = delta.magnitude();
    float overlap = (o1.boundary() + o2.boundary()) - distance;
    if (overlap > 0) {
      Vector correction = delta.normalise().scale(overlap / 2f);
      o1.position().sub(correction);
      o2.position().add(correction);
      return true;
    }
    return false;
  }

  private void applyConstraints(List<? extends VerletObject> objects) {
    for (VerletObject o : objects) {
      if (constraint != null) {
        constraint.applyConstraint(o);
      }
    }
  }

  private abstract class Constraint {
    abstract void applyConstraint(VerletObject o);

    abstract boolean contains(VerletObject o);

    abstract Vector centre();
  }

  private class RectangularBorder extends Constraint {
    private float width;
    private float height;

    RectangularBorder(float width, float height) {
      this.width = width;
      this.height = height;
    }

    boolean contains(VerletObject o) {
      float x = o.position().x;
      float y = o.position().y;
      float r = o.boundary();
      return (x + r > width || x - r < 0 || y + r > height || y - r < 0);
    }

    Vector centre() {
      return new Vector(width / 2f, height / 2f);
    }

    void applyConstraint(VerletObject o) {
      if (o == null)
        return;
      float x = o.position().x;
      float y = o.position().y;
      float r = o.boundary();
      Vector vel = o.velocity();
      if (x + r > width) {
        o.position().set(width - r, y);
        vel.x *= -damping;
        o.previousPosition().set(Vector.sub(o.position(), vel));
      } else if (x - r < 0) {
        o.position().set(r, y);
        vel.x *= -damping;
        o.previousPosition().set(Vector.sub(o.position(), vel));
      } else if (y + r > height) {
        o.position().set(x, height - r);
        vel.y *= -damping;
        o.previousPosition().set(Vector.sub(o.position(), vel));
      } else if (y - r < 0) {
        o.position().set(x, r);
        vel.y *= -damping;
        o.previousPosition().set(Vector.sub(o.position(), vel));
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

    boolean contains(VerletObject o) {
      Vector origin = Vector.sub(o.position(), centre);
      return (origin.magnitude() > radius - o.boundary());
    }

    Vector centre() {
      return centre;
    }

    void applyConstraint(VerletObject o) {
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
