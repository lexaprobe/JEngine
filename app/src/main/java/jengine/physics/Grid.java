package jengine.physics;

import jengine.objects.SimObject;

import java.util.List;
import java.util.function.BiConsumer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

class Grid {
  private float cellSize;
  private final Int2ObjectOpenHashMap<IntArrayList> cells;

  public Grid(float cellSize) {
    if (cellSize <= 0f) {
      throw new IllegalArgumentException("cell size must be greater than zero");
    }
    this.cellSize = cellSize;
    this.cells = new Int2ObjectOpenHashMap<>();
  }

  public void rebuild(List<? extends SimObject> objects) {
    cells.clear();
    // we use index-based iteration because we want to store the index of an object in a cell
    for (int i = 0; i < objects.size(); i++) {
      SimObject o = objects.get(i);
      if (o == null)
        continue;
      int minX = worldToCell(o.minX());
      int maxX = worldToCell(o.maxX());
      int minY = worldToCell(o.minY());
      int maxY = worldToCell(o.maxY());
      for (int x = minX; x <= maxX; x++) {
        for (int y = minY; y <= maxY; y++) {
          int key = cellKey(x, y);
          cells.computeIfAbsent(key, k -> new IntArrayList()).add(i);
        }
      }
    }
  }

  public void forEach(BiConsumer<Integer, Integer> consumer) {
    for (IntArrayList cell : cells.values()) {
      int size = cell.size();
      for (int i = 0; i < size; i++) {
        int o1 = cell.getInt(i);
        for (int j = i + 1; j < size; j++) {
          int o2 = cell.getInt(j);
          consumer.accept(o1, o2);
        }
      }
    }
  }

  private int worldToCell(float coord) {
    return (int) Math.floor(coord / cellSize);
  }

  private static int cellKey(int x, int y) {
    return (x & 0xFFFF) << 16 | (y & 0xFFFF);
  }
}
