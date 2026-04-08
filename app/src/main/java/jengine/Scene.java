package jengine;

import jengine.objects.VerletObject;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Scene {
  private final List<VerletObject> objects = new ArrayList<>();

  private int objectCount = 0;
  private float objectHue = 0f;
  private float objectHueStep = 0.02f;

  public void addObject(VerletObject o) {
    objects.add(o);
    objectCount++;
  }

  public List<VerletObject> objects() {
    return objects;
  }

  public int numObjects() {
    return objectCount;
  }

  public boolean removeObject(VerletObject o) {
    objectCount--;
    return objects.remove(o);
  }

  public void clearScene() {
    objectCount = 0;
    objects.clear();
  }

  public void setObjHueStep(float step) {
    objectHueStep = step;
  }

  public int[] getObjColour(int colourMode) {
    int[] rgb = new int[] {255, 255, 255};
    switch (colourMode) {
      case JEngine.COLOUR_RAINBOW -> {
        int bin = Color.HSBtoRGB(objectHue, 1, 1);
        rgb[0] = (bin >> 16) & 0xFF;
        rgb[1] = (bin >> 8) & 0xFF;
        rgb[2] = bin & 0xFF;
        objectHue += objectHueStep % 1;
      }
      case JEngine.COLOUR_RANDOM -> {
        for (int i = 0; i < 3; i++) {
          rgb[i] = Util.randomInt(0, 255);
        }
      }
    }
    return rgb;
  }
}
