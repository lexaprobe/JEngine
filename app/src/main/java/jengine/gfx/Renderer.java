package jengine.gfx;

import jengine.Scene;
import jengine.objects.SimObject;
import jengine.objects.Atom;

public class Renderer {
  public static final int[] WHITE = new int[] {255, 255, 255};
  public static final int[] GRAY = new int[] {180, 180, 180};
  public static final int[] BLACK = new int[] {0, 0, 0};

  private final GfxBackend gfx = new GfxBackend();

  public void renderScene(Scene scene) {
    gfx.clear();
    for (SimObject o : scene.bgObjects()) {
      if (o instanceof Atom a) {
        drawCircle(a.position().components(), a.radius(), a.colour());
      }
    }
    for (SimObject o : scene.objects()) {
      if (o instanceof Atom a)
        drawCircle(a.position().components(), a.radius(), a.colour());
    }
  }

  public void drawCircle(float[] coordinates, float radius, int[] colour) {
    float x = coordinates[0];
    float y = coordinates[1];
    float[] rgb = normaliseColour(colour);
    gfx.drawCircle(x, y, radius, rgb);
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
    gfx.setClearColour(normaliseColour(rgb));
  }
}
