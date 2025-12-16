package jengine;

import java.util.Random;

class Util {
  private static final Random random = new Random();

  public static int randomInt(int min, int max) {
    return random.nextInt(max - min + 1) + min;
  }

  public static float randomFloat(float min, float max) {
    return random.nextFloat(max - min + 1) + min;
  }
}
