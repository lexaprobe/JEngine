package jengine;

import jengine.physics.PhysicsWorld;
import jengine.objects.SimObject;
import jengine.objects.Atom;
import jengine.objects.DynamicAtom;
import jengine.objects.StaticAtom;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Scene {
  private final List<SimObject> bgObjects = new ArrayList<>();
  private final List<SimObject> objects = new ArrayList<>();

  private int objectCount = 0;
  private float objectHue = 0f;
  private float objectHueStep = 0.02f;
  private int colourMode = JEngine.COLOUR_DEFAULT;

  public void addBgObject(SimObject o) {
    bgObjects.add(o);
  }

  public List<SimObject> bgObjects() {
    return bgObjects;
  }

  public List<SimObject> objects() {
    return objects;
  }

  public void clean(PhysicsWorld world) {
    List<SimObject> toRemove = new ArrayList<>();
    for (SimObject o : objects) {
      if (!world.holds(o))
        toRemove.add(o);
    }
    for (SimObject o : toRemove) {
      removeObject(o);
    }
  }

  public int numObjects() {
    return objectCount;
  }

  public boolean removeObject(SimObject o) {
    objectCount--;
    return objects.remove(o);
  }

  public void clearBgObjects() {
    bgObjects.clear();
  }

  public void clearScene() {
    objectCount = 0;
    objects.clear();
  }

  public void setColourMode(int mode) {
    this.colourMode = mode;
  }

  public void setObjHueStep(float step) {
    this.objectHueStep = step;
  }

  /* object spawning */

  public StaticAtom spawnObjectStatic(float[] pos, float radius, float mass) {
    StaticAtom atom = new StaticAtom(pos, radius, mass);
    objects.add(atom);
    objectCount++;
    return atom;
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius, float mass, float[] vel) {
    DynamicAtom atom = new DynamicAtom(pos, radius, mass, vel);
    objects.add(atom);
    objectCount++;
    return atom;
  }

  /* object spawn-values handling */

  public float getRandomRadius() {
    return Util.randomInt((int) Atom.RADIUS_SMALL, (int) Atom.RADIUS_LARGE);
  }

  public float[] getRandomVelocity(float dt) {
    return scaleVelocity(
        new float[] {Util.randomInt(0, JEngine.OBJ_VMAX), Util.randomInt(0, JEngine.OBJ_VMAX)}, dt);
  }

  public int[] getObjColour() {
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

  public float[] scaleVelocity(float[] vel, float dt) {
    float[] scaled = new float[2];
    for (int i = 0; i < 2; i++) {
      scaled[i] = (float) vel[i] * dt;
    }
    return scaled;
  }

  /* overloading for static object spawners */

  public StaticAtom spawnObjectStatic(float[] pos) {
    return spawnObjectStatic(pos, Atom.RADIUS_DEFAULT, Atom.MASS_DEFAULT);
  }

  public StaticAtom spawnObjectStatic(float[] pos, float radius) {
    return spawnObjectStatic(pos, radius, Atom.MASS_DEFAULT);
  }

  /* overloading for dynamic object spawners for overloading */

  public DynamicAtom spawnObjectDynamic(float[] pos) {
    return spawnObjectDynamic(pos, Atom.RADIUS_DEFAULT, Atom.MASS_DEFAULT);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius) {
    return spawnObjectDynamic(pos, radius, Atom.MASS_DEFAULT);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float[] vel) {
    return spawnObjectDynamic(pos, Atom.RADIUS_DEFAULT, Atom.MASS_DEFAULT, vel);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius, float[] vel) {
    return spawnObjectDynamic(pos, radius, Atom.MASS_DEFAULT, vel);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius, float mass) {
    DynamicAtom atom = new DynamicAtom(pos, radius, mass);
    objects.add(atom);
    objectCount++;
    return atom;
  }
}
