package jengine;

import jengine.gfx.Renderer;
import jengine.gfx.Window;
import jengine.physics.PhysicsWorld;
import jengine.objects.DynamicAtom;
import jengine.objects.StaticAtom;

public class JEngine {
  private final int targetFPS = 120;
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
    float dt = 1 / targetFPS;
    double prevTime = window.time();
    spawnObjectDynamic(world, world.centre());

    while (!window.shouldClose()) {
      double currentTime = window.time();
      double deltaTime = currentTime - prevTime;
      prevTime = currentTime;

      window.pollEvents();
      // check here if a certain key is pressed
      if (deltaTime >= dt) {
        world.step(dt);
        renderer.renderScene(world);
        window.swapBuffers();
      }
    }

    window.terminate();
  }

  public void spawnObjectStatic(PhysicsWorld world, float[] pos) {
    world.addObject(new StaticAtom(1, 10f, pos));
  }

  public void spawnObjectDynamic(PhysicsWorld world, float[] pos) {
    world.addObject(new DynamicAtom(1, 10f, pos));
  }

  public static void main(String[] args) {
    new JEngine(800, 800).run();
  }
}
