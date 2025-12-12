package jengine;

import jengine.gfx.Renderer;
import jengine.gfx.Window;
import jengine.physics.PhysicsWorld;

public class JEngine {
  private final int targetFPS = 120;
  private final Renderer renderer;
  private final PhysicsWorld world;

  public JEngine(int width, int height) {
    Window window = new Window(width, height);
    renderer = new Renderer(window);
    world = new PhysicsWorld(width, height);
  }

  public void init() {
    renderer.start();
  }

  public void simulate() {}

  public void spawnObject() {}

  public void drawObjects() {}

  public static void main(String[] args) {
    new JEngine(800, 800).init();
  }
}
