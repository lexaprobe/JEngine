package jengine.gfx;

import jengine.objects.Atom;
import jengine.objects.VerletObject;

import java.util.List;

public class Renderer {
  public static final int[] WHITE = new int[] {255, 255, 255};
  public static final int[] GRAY = new int[] {180, 180, 180};
  public static final int[] BLACK = new int[] {0, 0, 0};

  private final Window window;

  private int width;
  private int height;

  public Renderer(int width, int height) {
    window = new Window(width, height);
    this.width = width;
    this.height = height;
    window.init();
  }

  public void renderObjects(List<VerletObject> objects) {
    Graphics.clear();
    for (VerletObject o : objects) {
      if (o instanceof Atom atom) {
        drawCircle(atom.position().components(), atom.radius(), atom.colour());
      }
    }
  }

  public void drawCircle(float[] position, float radius, int[] colour) {
    float x = position[0];
    float y = position[1];
    float[] rgb = normaliseColour(colour);
    Graphics.drawCircle(x, y, radius, rgb);
  }

  public void drawRectangle(float[] position, float width, float height, int[] colour) {
    float x = position[0];
    float y = position[1];
    float[] rgb = normaliseColour(colour);
    Graphics.drawRectangle(x, y, width, height, rgb);
  }

  public float[] normaliseColour(int[] colour) {
    float[] normalised = new float[3];
    for (int i = 0; i < 3; i++) {
      normalised[i] = colour[i] / 255f;
    }
    return normalised;
  }

  public void drawText(String text) {}

  public void setBgColour(int[] rgb) {
    Graphics.setClearColour(normaliseColour(rgb));
  }

  public float windowWidth() {
    return width;
  }

  public float windowHeight() {
    return height;
  }

  public double time() {
    return window.time();
  }

  public boolean shouldClose() {
    return window.shouldClose();
  }

  public void terminate() {
    window.terminate();
  }

  public void setWindowTitle(String title) {
    window.setWindowTitle(title);
  }

  public void swapBuffers() {
    window.swapBuffers();
  }

  public void pollEvents() {
    window.pollEvents();
  }

  public float[] mouseClicked() {
    return window.mouseClicked();
  }

  public int getKey() {
    return window.getKey();
  }
}
