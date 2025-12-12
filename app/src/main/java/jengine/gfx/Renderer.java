package jengine.gfx;

import jengine.objects.Atom;
import jengine.physics.PhysicsWorld;

public class Renderer {
  private final GfxBackend gfx = new GfxBackend();

  public void renderScene(PhysicsWorld world) {
    gfx.clear();
    for (Atom a : world.getObjects()) {
      drawCircle(world, a.position().components(), a.radius(), a.colour());
    }
  }

  public void drawCircle(PhysicsWorld world, float[] coordinates, float radius, int[] colour) {
    // normalise components
    float x = coordinates[0];
    float y = coordinates[1];
    float[] rgb = normaliseColour(colour);
    gfx.drawCircle(x, y, radius, rgb);
  }

  public float[] normaliseColour(int[] colour) {
    float[] normalised = new float[3];
    for (int i = 0; i < 3; i++) {
      normalised[i] = colour[i] / 255;
    }
    return normalised;
  }

  public void drawText(String text) {}
}
