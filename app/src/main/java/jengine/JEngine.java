package jengine;

import jengine.gfx.Renderer;
import jengine.gfx.Window;
import jengine.physics.PhysicsWorld;
import jengine.objects.Atom;
import jengine.objects.DynamicAtom;
import jengine.objects.StaticAtom;

import java.util.Random;

public class JEngine {
  private final int targetFPS = 60;
  private final float dt = 1f / targetFPS;
  private final PhysicsWorld world;
  private final Renderer renderer;
  private final Window window;
  private final Random random = new Random();

  private float objectRadiusBound = 20f;

  public static final int VMAX = 500;

  public JEngine(int width, int height) {
    world = new PhysicsWorld(width, height);
    renderer = new Renderer();
    window = new Window(width, height);
  }

  public void run() {
    window.init();
    while (!window.shouldClose()) {
      try {
        Thread.sleep((long) (dt * 1000));
      } catch (InterruptedException e) {
      }
      window.pollEvents();
      float[] coords = window.mouseClicked();
      if (coords != null) {
        spawnObjectDynamic(coords, getRandomVelocity());
      }
      // check here if a certain key is pressed
      world.step(dt, 1);
      renderer.renderScene(world);
      window.swapBuffers();
    }
    window.terminate();
  }

  public StaticAtom spawnObjectStatic(float[] pos, float radius, float mass) {
    StaticAtom atom = new StaticAtom(pos, radius, mass);
    world.addObject(atom);
    return atom;
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius, float mass, float[] vel) {
    DynamicAtom atom = new DynamicAtom(pos, radius, mass, vel);
    world.addObject(atom);
    return atom;
  }

  public float getRandomRadius() {
    return random.nextFloat(2 * Atom.DEFAULT_RADIUS) - objectRadiusBound;
  }

  public float[] getRandomVelocity() {
    int x = random.nextInt(VMAX);
    int y = random.nextInt(VMAX);
    return scaleVelocity(new float[] {x, y});
  }

  public int[] getRandomColour() {
    int r = random.nextInt(256);
    int g = random.nextInt(256);
    int b = random.nextInt(256);
    return new int[] {r, g, b};
  }

  public float[] scaleVelocity(float[] vel) {
    float[] scaled = new float[2];
    for (int i = 0; i < 2; i++) {
      scaled[i] = (float) vel[i] * dt;
    }
    return scaled;
  }

  /* static object spawners */

  public StaticAtom spawnObjectStatic(float[] pos) {
    return spawnObjectStatic(pos, Atom.DEFAULT_RADIUS, Atom.DEFAULT_MASS);
  }

  public StaticAtom spawnObjectStatic(float[] pos, float radius) {
    return spawnObjectStatic(pos, radius, Atom.DEFAULT_MASS);
  }

  /* dynamic object spawners */

  public DynamicAtom spawnObjectDynamic(float[] pos) {
    return spawnObjectDynamic(pos, Atom.DEFAULT_RADIUS, Atom.DEFAULT_MASS);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius) {
    return spawnObjectDynamic(pos, radius, Atom.DEFAULT_MASS);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float[] vel) {
    return spawnObjectDynamic(pos, Atom.DEFAULT_RADIUS, Atom.DEFAULT_MASS, vel);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius, float[] vel) {
    return spawnObjectDynamic(pos, radius, Atom.DEFAULT_MASS, vel);
  }

  public DynamicAtom spawnObjectDynamic(float[] pos, float radius, float mass) {
    DynamicAtom atom = new DynamicAtom(pos, radius, mass);
    world.addObject(atom);
    return atom;
  }

  public static void main(String[] args) {
    new JEngine(800, 800).run();
  }
}
