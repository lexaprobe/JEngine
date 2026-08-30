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
  public static final int METRES_PER_PIXEL = 32;

  /* Engine components */
  private final PhysicsWorld world;
  private final Renderer renderer;
  private final Scene scene;
  private final Pendulum pendulum;

  /* User-defined variables */
  private double deltaTime = 1f / 60f;
  private int simulationSubSteps = 2;
  private int colourMode = COLOUR_DEFAULT;

  /* Simulation toggles */
  private boolean toggleSpawn = true;
  private boolean togglePause = false;

  public JEngine(int width, int height, int borderMode) {
    renderer = new Renderer(width, height);
    float scaledWidth = width / METRES_PER_PIXEL;
    float scaledHeight = width / METRES_PER_PIXEL;
    float[] centre = new float[] {scaledWidth / 2f, scaledHeight / 2f};
    switch (borderMode) {
      case BORDER_NONE:
        world = new PhysicsWorld(centre);
        break;
      case BORDER_CIRCLE:
        world = new PhysicsWorld(centre, scaledWidth / 2.5f);
        break;
      default:
        world = new PhysicsWorld(centre, scaledWidth, scaledHeight);
    }
    scene = new Scene();
    pendulum = new Pendulum(0.9f, 0.005f);
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
    int updates = 0;

    int fps = 0;
    int ups = 0;

    double previousTime = renderer.time();
    double accumulator = 0.0;
    double currentTime;
    double frameTime;

    double timer = 0.0;

    while (!renderer.shouldClose()) {
      currentTime = renderer.time();
      frameTime = currentTime - previousTime;
      previousTime = currentTime;

      accumulator += frameTime;
      timer += frameTime;

      pollEvents();

      while (accumulator >= deltaTime) {
        updateScene();
        accumulator -= deltaTime;
        updates++;
      }

      renderScene(fps, ups);
      frames++;

      if (timer >= 1.0) {
        fps = frames;
        ups = updates;
        frames = 0;
        updates = 0;
        timer = 0.0;
      }
    }
    renderer.terminate();
  }

  private void pollEvents() {
    renderer.pollEvents();
    // For now, any mouse click spawns an Atom at that location
    pollMouseClick(renderer.mouseClicked());
    pollKeyPress(renderer.getKey());
  }

  private void updateScene() {
    if (!togglePause) {
      world.step(scene.objects(), deltaTime, simulationSubSteps);
      if (!toggleSpawn) {
        pendulum.step();
        pendulum.spawnAtom();
      }
    }
  }

  private void renderScene(int fps, int ups) {
    renderer.renderObjects(scene.objects());
    renderer.setWindowTitle("FPS: " + fps + " | UPS: " + ups + " | Objects: " + scene.numObjects());
    renderer.swapBuffers();
  }

  private void pollMouseClick(float[] coords) {
    if (coords == null)
      return;
    Vector vel = new Vector();
    float mass = VerletObject.MASS_DEFAULT;
    float radius = Util.randomFloat(Atom.RADIUS_MIN, Atom.RADIUS_LARGE);
    Atom atom = new Atom(new Vector(coords[0] / METRES_PER_PIXEL, coords[1] / METRES_PER_PIXEL),
        vel, mass, radius, scene.getObjColour(colourMode));
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
      float xvel = mass * (float) Math.sin(theta);
      float yvel = Math.abs(mass * (float) Math.cos(theta));
      Vector velocity = new Vector(xvel, yvel).scale(deltaTime);
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
    world.setGravity(new Vector(0.0f, 9.81f));
    engine.run();
  }
}
