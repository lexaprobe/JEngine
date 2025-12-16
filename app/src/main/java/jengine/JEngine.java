package jengine;

import jengine.gfx.Renderer;
import jengine.gfx.Window;
import jengine.objects.Atom;
import jengine.objects.StaticAtom;
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

  public static final int ACTION_CLEAR = 100;
  public static final int ACTION_PAUSE = 101;
  public static final int ACTION_PULL = 102;

  public static final int OBJ_VMAX = 750;
  public static final int OBJ_LIMIT = 1000;

  private final int targetFPS = 120;
  private final float dt = 1f / targetFPS;
  private final PhysicsWorld world;
  private final Renderer renderer;
  private final Scene scene;
  private final Window window;

  private int spawnMode = SPAWN_DEFAULT;
  private int supSteps = 2;
  private boolean paused = false;

  public JEngine(int width, int height) {
    world = new PhysicsWorld(width, height);
    renderer = new Renderer();
    scene = new Scene();
    window = new Window(width, height);
    window.init();
  }

  public void setSpawnMode(int mode) {
    spawnMode = mode;
  }

  public void setColourMode(int mode) {
    scene.setColourMode(mode);
  }

  public void setBorderMode(int mode) {
    world.setBorder(mode);
    if (mode == BORDER_CIRCLE) {
      renderer.setBgColour(Renderer.GRAY);
      StaticAtom circle = new StaticAtom(window.centre(), world.width() / 2.5f, 1);
      circle.paint(Renderer.BLACK);
      scene.addBgObject(circle);
    } else {
      renderer.setBgColour(Renderer.BLACK);
      scene.clearBgObjects();
    }
  }

  public void run() {
    int frames = 0;
    double fps = targetFPS;
    double previousTime = window.time();
    while (!window.shouldClose()) {
      double currentTime = window.time();
      frames++;
      if (!paused && spawnMode == SPAWN_AUTO && frames % 2 == 0 && fps >= 60) {
        Atom a = scene.spawnObjectDynamic(new float[] {5, 10}, Atom.RADIUS_SMALL,
            scene.scaleVelocity(new float[] {295, 121}, dt));
        a.paint(scene.getObjColour());
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
    world.setGravity(new float[] {0f, 500f});
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
      case ACTION_PULL -> {
        float x = Util.randomInt(-10000, 10000);
        float y = Util.randomInt(-10000, 10000);
        world.setGravity(new float[] {x, y});
      }
    }
  }

  public static void main(String[] args) {
    JEngine engine = new JEngine(800, 800);
    engine.setSpawnMode(SPAWN_AUTO);
    engine.setBorderMode(BORDER_RECT);
    engine.setColourMode(COLOUR_RAINBOW);
    engine.run();
  }
}
