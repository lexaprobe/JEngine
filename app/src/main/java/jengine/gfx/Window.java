package jengine.gfx;

import jengine.JEngine;

import java.nio.IntBuffer;
import java.nio.DoubleBuffer;
import java.util.Queue;
import java.util.LinkedList;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window {
  private long window;
  private int width;
  private int height;
  private Queue<double[]> mouseClicks = new LinkedList<>();
  private Queue<Integer> keysPressed = new LinkedList<>();

  public Window(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void init() {
    GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit())
      throw new IllegalStateException("Unable to initialise GLFW");

    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

    // create the window
    window = glfwCreateWindow(width, height, "JEngine", NULL, NULL);
    if (window == NULL)
      throw new RuntimeException("Failed to create the GLFW window");

    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
        glfwSetWindowShouldClose(window, true);
      else if (key == GLFW_KEY_C && action == GLFW_RELEASE)
        keysPressed.offer(JEngine.ACTION_CLEAR);
      else if (key == GLFW_KEY_P && action == GLFW_RELEASE)
        keysPressed.offer(JEngine.ACTION_PAUSE);
    });

    glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
      if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS)
        mouseClicks.offer(getCursorPos());
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

    GL.createCapabilities();
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0, width, height, 0, -1, 1);

    glMatrixMode(GL_MODELVIEW);
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(window);
  }

  public float[] centre() {
    return new float[] {width / 2f, height / 2f};
  }

  public void setWindowTitle(String s) {
    glfwSetWindowTitle(window, s);
  }

  public void pollEvents() {
    glfwPollEvents();
  }

  public void swapBuffers() {
    glfwSwapBuffers(window);
  }

  public double time() {
    return glfwGetTime();
  }

  public int getKey() {
    if (keysPressed.isEmpty())
      return 0;
    return keysPressed.poll();
  }

  public double[] getCursorPos() {
    try (MemoryStack stack = stackPush()) {
      DoubleBuffer xpos = stack.mallocDouble(1);
      DoubleBuffer ypos = stack.mallocDouble(1);
      glfwGetCursorPos(window, xpos, ypos);
      return new double[] {xpos.get(0), ypos.get(0)};
    }
  }

  public float[] mouseClicked() {
    if (mouseClicks.isEmpty())
      return null;
    double[] coords = mouseClicks.poll();
    return new float[] {(float) coords[0], (float) coords[1]};
  }

  public void terminate() {
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }
}
