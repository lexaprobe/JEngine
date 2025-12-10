package jengine.physics;

public class Atom {
  private float mass;
  private float radius;
  private Vector position;
  private Vector previousPosition;
  private Vector acceleration = new Vector();
  private Integer[] colour = new Integer[] {255, 255, 255};

  public Atom(float mass, float radius) {
    this.mass = mass;
    this.radius = radius;
    this.position = new Vector();
    this.previousPosition = new Vector();
  }

  public Atom(float mass, float radius, Float[] position) {
    this.mass = mass;
    this.radius = radius;
    if (position.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 positional components, got " + position.length);
    }
    this.position = new Vector(position);
    this.previousPosition = this.position.clone();
  }

  public Atom(float mass, float radius, Float[] position, Float[] velocity) {
    this.mass = mass;
    this.radius = radius;
    if (position.length != 2) {
      throw new IllegalArgumentException(
          "expected 2 positional components, got " + position.length);
    }
    this.position = new Vector(position);
    if (velocity.length != 2) {
      throw new IllegalArgumentException("expected 2 velocity components, got " + velocity.length);
    }
    this.previousPosition = Vector.sub(this.position, new Vector(velocity));
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

  public Vector previousPosition() {
    return previousPosition;
  }

  public Vector velocity() {
    return Vector.sub(position, previousPosition);
  }

  public Vector acceleration() {
    return acceleration;
  }

  public Integer[] colour() {
    return colour;
  }

  public float distanceTo(Atom atom) {
    if (atom == null) {
      return 0f;
    }
    Vector p1 = this.position();
    Vector p2 = atom.position();
    float xDiff = p2.x - p1.x;
    float yDiff = p2.y = p1.y;
    double distance = Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
    return (float) distance;
  }

  public void accelerate(Float[] components) {
    this.acceleration.set(components);
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
