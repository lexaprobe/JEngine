package jengine.gfx;

import jengine.JEngine;
import jengine.Scene;
import jengine.objects.SimObject;
import jengine.objects.Rectangle;
import jengine.objects.Atom;
import jengine.objects.DynamicAtom;

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
      } else if (o instanceof Rectangle x) {
        drawRectangle(x.position().components(), x.width(), x.height(), x.colour());
      }
    }
    for (SimObject o : scene.objects()) {
      if (o instanceof Atom a) {
        int[] colour = a.colour();
        if (a instanceof DynamicAtom d && scene.colourMode() == JEngine.COLOUR_VEL) {
          float value = Math.clamp(d.velocity().magnitude(), 0, 1f);
          colour = new int[] {(int) (value * 255), 0, (int) (255 * (1f - value))};
        }
        drawCircle(a.position().components(), a.radius(), colour);
      }
    }
  }

  public void drawCircle(float[] position, float radius, int[] colour) {
    float x = position[0];
    float y = position[1];
    float[] rgb = normaliseColour(colour);
    gfx.drawCircle(x, y, radius, rgb);
  }

  public void drawRectangle(float[] position, float width, float height, int[] colour) {
    float x = position[0];
    float y = position[1];
    float[] rgb = normaliseColour(colour);
    gfx.drawRectangle(x, y, width, height, rgb);
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
