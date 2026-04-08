package jengine;

import java.util.ArrayList;
import java.util.List;
import jengine.gfx.Renderer;
import jengine.objects.Atom;
import jengine.objects.VerletObject;
import jengine.physics.Vector;
import jengine.physics.PhysicsWorld;

public class JEngine {
  /* Engine settings */
  public static final int COLOUR_RAINBOW = 10;
  public static final int COLOUR_VEL = 11;
  public static final int COLOUR_NONE = 12;
  public static final int COLOUR_RANDOM = 13;
  public static final int COLOUR_DEFAULT = COLOUR_NONE;

  public static final int BORDER_RECT = 20;
  public static final int BORDER_CIRCLE = 21;
  public static final int BORDER_NONE = 22;
  public static final int BORDER_DEFAULT = BORDER_RECT;

  public static final int ACTION_CLEAR = 100;
  public static final int ACTION_PAUSE = 101;
  public static final int ACTION_TOGGLE_SPAWN = 102;
  public static final int ACTION_GRAVITATE = 103;

  public static final int OBJ_VMAX = 750;
  public static final int OBJ_LIMIT = 10000;

  /* Engine components */
  private final PhysicsWorld world;
  private final Renderer renderer;
  private final Scene scene;
  private final Pendulum pendulum;

  /* User-defined variables */
  private float targetFPS = 120;
  private int simulationSubSteps = 2;
  private int colourMode = COLOUR_DEFAULT;

  /* Simulation toggles */
  private boolean toggleSpawn = true;
  private boolean togglePause = false;
  private boolean toggleGravity = false;

  public JEngine(int width, int height, int borderMode) {
    float[] centre = new float[] {width / 2f, height / 2f};
    switch (borderMode) {
      case BORDER_NONE:
        world = new PhysicsWorld(centre);
        break;
      case BORDER_CIRCLE:
        world = new PhysicsWorld(centre, width / 2.5f);
        break;
      default:
        world = new PhysicsWorld(centre, width, height);
    }
    renderer = new Renderer(width, height);
    scene = new Scene();
    pendulum = new Pendulum(0.9f, 0.005f);
  }

  public void setTargetFPS(int fps) {
    if (fps <= 0) {
      throw new IllegalArgumentException("FPS must be greater than 0");
    }
    targetFPS = fps;
  }

  public void setSubSteps(int subSteps) {
    if (subSteps < 1) {
      throw new IllegalArgumentException("Sub steps must be greater than 0");
    }
    simulationSubSteps = subSteps;
  }

  public void setColourMode(int mode) {
    colourMode = mode;
  }

  public PhysicsWorld world() {
    return world;
  }

  public Scene scene() {
    return scene;
  }

  public void run() {
    int frames = 0;
    double fps = targetFPS;
    double previousTime = renderer.time();
    while (!renderer.shouldClose()) {
      double currentTime = renderer.time();
      frames++;
      if (!togglePause && !toggleSpawn && frames % 2 == 0) {
        pendulum.step();
        pendulum.spawnAtom();
      }
      if (currentTime - previousTime >= 1.0f) {
        fps = frames;
        frames = 0;
        previousTime = currentTime;
      }
      updateScene(fps);
    }
    renderer.terminate();
  }

  private void updateScene(double currentFPS) {
    pollEvents();
    if (!togglePause)
      world.step(scene.objects(), 1f / targetFPS, simulationSubSteps);
    renderer.renderObjects(scene.objects());
    renderer.setWindowTitle("FPS: " + (int) currentFPS + " | Objects: " + scene.numObjects());
    renderer.swapBuffers();
  }

  private void pollEvents() {
    renderer.pollEvents();
    pollMouseClick(renderer.mouseClicked());
    pollKeyPress(renderer.getKey());
  }

  private void pollMouseClick(float[] coords) {
    if (coords == null)
      return;
    // Vector vel = Util.randomVector(OBJ_VMAX).scale(1f / targetFPS);
    Vector vel = new Vector();
    // float mass = Util.randomFloat(0, Float.MAX_VALUE);
    float mass = VerletObject.MASS_DEFAULT;
    float radius = Util.randomFloat(Atom.RADIUS_MIN, Atom.RADIUS_LARGE);
    Atom atom = new Atom(new Vector(coords), vel, mass, radius, scene.getObjColour(colourMode));
    scene.addObject(atom);
  }

  private void pollKeyPress(int key) {
    if (key == 0)
      return;
    switch (key) {
      case ACTION_CLEAR -> {
        scene.clearScene();
      }
      case ACTION_PAUSE -> {
        togglePause = !togglePause;
      }
      case ACTION_TOGGLE_SPAWN -> {
        toggleSpawn = !toggleSpawn;
      }
      case ACTION_GRAVITATE -> {
        toggleGravity = !toggleGravity;
      }
    }
  }

  public void cleanScene() {
    List<VerletObject> toRemove = new ArrayList<>();
    for (VerletObject o : scene.objects()) {
      if (o == null)
        continue;
      float x = o.position().x;
      float y = o.position().y;
      float r = o.boundary();
      if (x + r > 2 * o.maxX() + renderer.windowWidth() || x - r < -2 * o.maxX()
          || y + r > 2 * o.maxY() + renderer.windowHeight() || y - r < -2 * o.maxY()) {
        toRemove.add(o);
      }
    }
    for (VerletObject o : toRemove) {
      scene.removeObject(o);
    }
  }

  private class Pendulum {
    private float theta = 0;
    private float maxTheta;
    private float step;
    private boolean angleIncreasing = true;

    Pendulum(float maxTheta, float step) {
      this.maxTheta = maxTheta;
      this.step = step;
    }

    void step() {
      if (theta == maxTheta)
        angleIncreasing = false;
      else if (theta == -maxTheta)
        angleIncreasing = true;
      if (angleIncreasing)
        theta += step;
      else
        theta -= step;
    }

    void spawnAtom() {
      float mass = VerletObject.MASS_DEFAULT;
      float radius = Atom.RADIUS_SMALL;
      float xvel = mass * 300f * (float) Math.sin(theta);
      float yvel = Math.abs(mass * 300f * (float) Math.cos(theta));
      Vector velocity = new Vector(xvel, yvel).scale(1f / targetFPS);
      Vector position = new Vector(world.centre());
      position.y *= 0.2f; // move position up
      Atom atom = new Atom(position, velocity, mass, radius, scene.getObjColour(colourMode));
      scene.addObject(atom);
    }
  }

  public static void main(String[] args) {
    JEngine engine = new JEngine(800, 800, BORDER_RECT);
    PhysicsWorld world = engine.world();
    world.setGravityMode(PhysicsWorld.GRAVITY_UNIFORM);
    world.setGravity(new Vector(0, 500));
    engine.setColourMode(COLOUR_RAINBOW);
    engine.run();
  }
}
