package jengine.gfx;

import static org.lwjgl.opengl.GL11.*;

class Graphics {

  public static void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public static void setClearColour(float[] colour) {
    glClearColor(colour[0], colour[1], colour[2], 1);
  }

  public static void drawCircle(float x, float y, float radius, float[] colour) {
    int steps = 20;
    float angle = (float) Math.PI * 2f / steps;
    float prevX = x;
    float prevY = y - radius;

    glColor3f(colour[0], colour[1], colour[2]);

    for (int i = 0; i <= steps; i++) {
      float newX = x + (float) (radius * Math.sin(angle * i));
      float newY = y + (float) (-radius * Math.cos(angle * i));

      glBegin(GL_TRIANGLES);
      glVertex2f(x, y);
      glVertex2f(prevX, prevY);
      glVertex2f(newX, newY);
      glEnd();

      prevX = newX;
      prevY = newY;
    }
  }

  public static void drawRectangle(float x, float y, float width, float height, float[] colour) {
    glColor3f(colour[0], colour[1], colour[2]);
    glBegin(GL_QUADS);
    glVertex2f(x, y);
    glVertex2f(x + width, y);
    glVertex2f(x + width, y + width);
    glVertex2f(x, y + width);
    glEnd();
  }
}
