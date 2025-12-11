package jengine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {
  private long window;
  private final int WINDOW_WIDTH = 800;
  private final int WINDOW_HEIGHT = 800;

  public void run() {
    init();
    loop();

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  private void init() {
    GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialise GLFW");
    }

    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

    // create the window
    window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "JEngine", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true);
      }
    });

    try (MemoryStack stack = stackPush()) {
      IntBuffer pWidth = stack.mallocInt(1);
      IntBuffer pHeight = stack.mallocInt(1);

      glfwGetWindowSize(window, pWidth, pHeight);
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2);
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
    glfwShowWindow(window);
  }

  private void loop() {
    GL.createCapabilities();
    glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

    while (!glfwWindowShouldClose(window)) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glfwSwapBuffers(window);
      glfwPollEvents();
    }
  }
}
