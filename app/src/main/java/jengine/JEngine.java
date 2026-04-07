package jengine;

import jengine.gfx.Renderer;
import jengine.gfx.Window;
import jengine.objects.Atom;
import jengine.objects.DynamicAtom;
import jengine.objects.Rectangle;
import jengine.physics.Vector;
import jengine.physics.PhysicsWorld;

public class JEngine {
  public static final int SPAWN_MANUAL = 0;
  public static final int SPAWN_AUTO = 1;
  public static final int SPAWN_DEFAULT = SPAWN_MANUAL;

  public static final int COLOUR_RAINBOW = 10;
  public static final int COLOUR_VEL = 11;
  public static final int COLOUR_NONE = 12;
  public static final int COLOUR_RANDOM = 13;
  public static final int COLOUR_DEFAULT = COLOUR_NONE;

  public static final int BORDER_RECT = 20;
  public static final int BORDER_CIRCLE = 21;
  public static final int BORDER_NONE = 22;
  public static final int BORDER_DEFAULT = BORDER_RECT;

  public static final int GRAVITY_UNIFORM = 30;
  public static final int GRAVITY_POINT = 31;
  public static final int GRAVITY_DEFAULT = GRAVITY_UNIFORM;

  public static final int LOCATION_TOP_LEFT = 40;
  public static final int LOCATION_PENDULUM = 41;
  public static final int LOCATION_DEFAULT = LOCATION_TOP_LEFT;

  public static final int ACTION_CLEAR = 100;
  public static final int ACTION_PAUSE = 101;
  public static final int ACTION_TOGGLE_SPAWN = 102;
  public static final int ACTION_GRAVITATE = 103;

  public static final int OBJ_VMAX = 750;
  public static final int OBJ_LIMIT = 1000;

  private final int targetFPS = 120;
  private final float dt = 1f / targetFPS;
  private final PhysicsWorld world;
  private final Renderer renderer;
  private final Scene scene;
  private final Window window;

  private int spawnMode = SPAWN_DEFAULT;
  private int spawnLocation = LOCATION_DEFAULT;
  private int supSteps = 2;
  private boolean spawn = true;
  private boolean paused = false;
  private boolean gravitating = false;
  private float theta = -0.9f;
  private boolean angleIncreasing = true;

  public JEngine(int width, int height) {
    world = new PhysicsWorld(width, height);
    renderer = new Renderer();
    scene = new Scene();
    window = new Window(width, height);
    // Rectangle footer = new Rectangle(new float[] {0, height}, width, 100);
    // footer.paint(Renderer.GRAY);
    // scene.addBgObject(footer);
    window.init();
  }

  public void setSpawnMode(int mode) {
    spawnMode = mode;
  }

  public void setSpawnLocation(int mode) {
    spawnLocation = mode;
  }

  public void setColourMode(int mode) {
    scene.setColourMode(mode);
  }

  public void setBorderMode(int mode) {
    world.setBorder(mode);
    if (mode == BORDER_CIRCLE) {
      spawnLocation = LOCATION_PENDULUM;
    }
  }

  public void setHueCycle(double step) {
    scene.setObjHueStep((float) step);
  }

  public void setGravityMode(int mode) {
    world.setGravityMode(mode);
  }

  public void run() {
    int frames = 0;
    double fps = targetFPS;
    double previousTime = window.time();
    while (!window.shouldClose()) {
      double currentTime = window.time();
      frames++;
      if (!paused && spawn && spawnMode == SPAWN_AUTO && frames % 2 == 0 && fps >= 60) {
        switch (spawnLocation) {
          case LOCATION_PENDULUM:
            spawnPendulum();
            break;
          case LOCATION_TOP_LEFT:
            spawnTopLeft();
            break;
        }
      }
      if (currentTime - previousTime >= 1.0f) {
        fps = frames;
        frames = 0;
        previousTime = currentTime;
      }
      updateScene(fps);
    }
    window.terminate();
  }

  private void updateScene(double fps) {
    pollEvents();
    if (!paused)
      world.step(scene.objects(), dt, supSteps);
    renderer.renderScene(scene);
    window.setWindowTitle("FPS: " + (int) fps + " | Objects: " + scene.numObjects());
    window.swapBuffers();
  }

  private void pollEvents() {
    window.pollEvents();
    if (spawnMode == SPAWN_MANUAL)
      pollMouseClick(window.mouseClicked());
    pollKeyPress(window.getKey());
  }

  private void pollMouseClick(float[] coords) {
    if (coords == null)
      return;
    Atom a = scene.spawnObjectDynamic(coords, scene.getRandomRadius(), scene.getRandomVelocity(dt));
    a.paint(scene.getObjColour());
  }

  private void pollKeyPress(int key) {
    if (key == 0)
      return;
    switch (key) {
      case ACTION_CLEAR -> {
        scene.clearScene();
      }
      case ACTION_PAUSE -> {
        paused = !paused;
      }
      case ACTION_TOGGLE_SPAWN -> {
        spawn = !spawn;
      }
      case ACTION_GRAVITATE -> {
        gravitating = !gravitating;
      }
    }
  }

  private void spawnTopLeft() {
    DynamicAtom a = scene.spawnObjectDynamic(new float[] {5, 10}, Atom.RADIUS_SMALL,
        scene.scaleVelocity(new float[] {295, 121}, dt));
    a.paint(scene.getObjColour());
  }

  private void spawnPendulum() {
    if (theta == 0.9)
      angleIncreasing = false;
    else if (theta == -0.9)
      angleIncreasing = true;
    if (angleIncreasing)
      theta += 0.005f;
    else
      theta -= 0.005f;
    DynamicAtom a = scene.spawnObjectDynamic(new float[] {world.width() / 2, world.height() / 4},
        Atom.RADIUS_SMALL);
    float xvel = a.mass() * 500f * (float) Math.sin(theta);
    float yvel = Math.abs(a.mass() * 500f * (float) Math.cos(theta));
    a.previousPosition().set(
        Vector.sub(a.position(), new Vector(scene.scaleVelocity(new float[] {xvel, yvel}, dt))));
    a.paint(scene.getObjColour());
  }

  public static void main(String[] args) {
    JEngine engine = new JEngine(800, 800);
    engine.setSpawnMode(SPAWN_AUTO);
    engine.setSpawnLocation(LOCATION_PENDULUM);
    engine.setBorderMode(BORDER_RECT);
    engine.setColourMode(COLOUR_RAINBOW);
    engine.setGravityMode(GRAVITY_POINT);
    engine.run();
  }
}
