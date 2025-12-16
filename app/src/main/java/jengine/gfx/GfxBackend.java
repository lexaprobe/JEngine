package jengine.gfx;

import static org.lwjgl.opengl.GL11.*;

class GfxBackend {

  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }

  public void setClearColour(float[] colour) {
    glClearColor(colour[0], colour[1], colour[2], 1);
  }

  public void drawCircle(float x, float y, float radius, float[] colour) {
    int steps = 25;
    float angle = (float) Math.PI * 2 / steps;
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
}
