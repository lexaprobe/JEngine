package jengine.gfx;

public class Renderer {

  private final Window window;

  public Renderer(Window window) {
    this.window = window;
  }

  public Integer[] windowSize() {
    return window.size();
  }

  public void start() {
    window.run();
  }

  public void drawCircle(Float[] coordinates, float radius, Integer[] colour) {}
}
