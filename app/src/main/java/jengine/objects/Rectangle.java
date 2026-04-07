package jengine.objects;

public class Rectangle extends SimObject {

  public Rectangle(float[] position, float width, float height) {
    super(position);
    this.width = width;
    this.height = height;
  }

  public float minX() {
    return 0;
  }

  public float minY() {
    return 0;
  }

  public float maxX() {
    return 0;
  }

  public float maxY() {
    return 0;
  }
}
