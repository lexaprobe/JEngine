package jengine;

import jengine.gfx.Renderer;
import jengine.gfx.Window;
import jengine.physics.PhysicsWorld;
import jengine.objects.DynamicAtom;
import jengine.objects.StaticAtom;

public class JEngine {
  private final int targetFPS = 60;
  private final PhysicsWorld world;
  private final Renderer renderer;
  private final Window window;

  public JEngine(int width, int height) {
    world = new PhysicsWorld(width, height);
    renderer = new Renderer();
    window = new Window(width, height);
  }

  public void run() {
    window.init();
    float dt = 1f / targetFPS;
    while (!window.shouldClose()) {
      try {
        Thread.sleep((long) (dt * 1000));
      } catch (InterruptedException e) {
      }
      window.pollEvents();
      float[] coords = window.mouseClicked();
      if (coords != null) {
        spawnObjectDynamic(world, coords);
      }
      // check here if a certain key is pressed
      world.step(dt, 8);
      renderer.renderScene(world);
      window.swapBuffers();
    }
    window.terminate();
  }

  public void spawnObjectStatic(PhysicsWorld world, float[] pos) {
    world.addObject(new StaticAtom(1, 30f, pos));
  }

  public void spawnObjectDynamic(PhysicsWorld world, float[] pos) {
    world.addObject(new DynamicAtom(1, 30f, pos));
  }

  public static void main(String[] args) {
    new JEngine(800, 800).run();
  }
}
