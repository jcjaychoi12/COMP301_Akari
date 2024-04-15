package com.comp301.a09akari.model;

import java.util.*;

public class ModelImpl implements Model {

  // Required Fields
  private PuzzleLibrary library;
  private int activePuzzleIndex;
  // private Map<Integer, ArrayList<Integer>> bulbs;
  private ArrayList<Map<Integer, ArrayList<Integer>>> bulbList;
  private List<ModelObserver> observers;

  // Created Fields
  private Puzzle activePuzzle;

  public ModelImpl( PuzzleLibrary library) {
    if (library == null) {
      throw new IllegalArgumentException();
    }

    PuzzleLibrary newLibrary = new PuzzleLibraryImpl();
    for (int i = 0; i < library.size(); i++) {
      newLibrary.addPuzzle(library.getPuzzle(i));
    }

    this.library = newLibrary;
    this.activePuzzleIndex = 0;
    // this.bulbs = new HashMap<>();

    this.bulbList = new ArrayList<>();
    for (int i = 0; i < library.size(); i++) {
      Map<Integer, ArrayList<Integer>> bulbs = new HashMap<>();
      this.bulbList.add(bulbs);
    }

    this.observers = new ArrayList<>();
    this.activePuzzle = this.library.getPuzzle(this.activePuzzleIndex);
  }

  @Override
  public void addLamp(int r, int c) {
    if ((r < 0) || (c < 0) || (r >= this.activePuzzle.getHeight()) || (c >= this.activePuzzle.getWidth())) {
      throw new IndexOutOfBoundsException();
    }

    CellType cell = this.activePuzzle.getCellType(r, c);
    if (cell != CellType.CORRIDOR) {
      throw new IllegalArgumentException();
    }

    Map<Integer, ArrayList<Integer>> bulbs = this.bulbList.get(this.activePuzzleIndex);

    if (bulbs.containsKey(r)) {
      bulbs.get(r).add(c);
    } else {
      ArrayList<Integer> column = new ArrayList<>();
      column.add(c);
      bulbs.put(r, column);
    }

    for (ModelObserver observer : this.observers) {
      observer.update(this);
    }
  }

  @Override
  public void removeLamp(int r, int c) {
    if ((r < 0) || (c < 0) || (r >= this.activePuzzle.getHeight()) || (c >= this.activePuzzle.getWidth())) {
      throw new IndexOutOfBoundsException();
    }

    CellType cell = this.activePuzzle.getCellType(r, c);
    if (cell != CellType.CORRIDOR) {
      throw new IllegalArgumentException();
    }

    Map<Integer, ArrayList<Integer>> bulbs = this.bulbList.get(this.activePuzzleIndex);

    if (bulbs.containsKey(r) && bulbs.get(r).contains(c)) {
      bulbs.get(r).remove((Integer)c);
    }

    for (ModelObserver observer : this.observers) {
      observer.update(this);
    }
  }

  @Override
  public boolean isLit(int r, int c) {
    int height = this.activePuzzle.getHeight();
    int width = this.activePuzzle.getWidth();

    if ((r < 0) || (c < 0) || (r >= height) || (c >= width)) {
      throw new IndexOutOfBoundsException();
    }

    CellType cell = this.activePuzzle.getCellType(r, c);
    if (cell != CellType.CORRIDOR) {
      throw new IllegalArgumentException();
    }

    if (isLamp(r, c)) {
      return true;
    }

    // Checking Rows - Start of the section
    int current = c;
    int rowStart = c;
    while ((current > 0) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(r, --current) == CellType.CORRIDOR)) {}
    if ((current == 0) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)) {
      rowStart = current;
    } else {
      rowStart = current + 1;
    }

    // Checking Rows - End of the section
    current = c;
    int rowEnd = c;
    while ((current < width - 1) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(r, ++current) == CellType.CORRIDOR)) {}
    if ((current == width - 1) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)) {
      rowEnd = current;
    } else {
      rowEnd = current - 1;
    }

    // Checking if row section contains a lamp
    for (int i = rowStart; i <= rowEnd; i++) {
      if (isLamp(r, i)) {
        return true;
      }
    }

    // Checking Columns - Start of the section
    current = r;
    int colStart = r;
    while ((current > 0) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(--current, c) == CellType.CORRIDOR)) {}
    if ((current == 0) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)) {
      colStart = current;
    } else {
      colStart = current + 1;
    }

    // Checking Columns - End of the section
    current = r;
    int colEnd = r;
    while ((current < height - 1) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(++current, c) == CellType.CORRIDOR)) {}
    if ((current == height - 1) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)) {
      colEnd = current;
    } else {
      colEnd = current - 1;
    }

    // Checking if column section contains a lamp
    for (int i = colStart; i <= colEnd; i++) {
      if (isLamp(i, c)) {
        return true;
      }
    }

    // Not Lit
    return false;
  }

  @Override
  public boolean isLamp(int r, int c) {
    if ((r < 0) || (c < 0) || (r >= this.activePuzzle.getHeight()) || (c >= this.activePuzzle.getWidth())) {
      throw new IndexOutOfBoundsException();
    }

    CellType cell = this.activePuzzle.getCellType(r, c);
    if (cell != CellType.CORRIDOR) {
      throw new IllegalArgumentException();
    }

    Map<Integer, ArrayList<Integer>> bulbs = this.bulbList.get(this.activePuzzleIndex);

    return (bulbs.containsKey(r) && bulbs.get(r).contains(c));
  }

  @Override
  public boolean isLampIllegal(int r, int c) {
    int height = this.activePuzzle.getHeight();
    int width = this.activePuzzle.getWidth();

    if ((r < 0) || (c < 0) || (r >= height) || (c >= width)) {
      throw new IndexOutOfBoundsException();
    }

    CellType cell = this.activePuzzle.getCellType(r, c);
    if (cell != CellType.CORRIDOR) {
      throw new IllegalArgumentException();
    }

    if (!isLamp(r, c)) {
      throw new IllegalArgumentException();
    }

    boolean found = false;

    // Checking Rows - Start of the section
    int current = c;
    int rowStart = c;
    while ((current > 0) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(r, --current) == CellType.CORRIDOR)) {}
    if ((current == 0) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)) {
      rowStart = current;
    } else {
      rowStart = current + 1;
    }

    // Checking Rows - End of the section
    current = c;
    int rowEnd = c;
    while ((current < width - 1) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(r, ++current) == CellType.CORRIDOR)) {}
    if ((current == width - 1) && (this.activePuzzle.getCellType(r, current) == CellType.CORRIDOR)) {
      rowEnd = current;
    } else {
      rowEnd = current - 1;
    }

    // Checking for illegal bulbs in the row section
    for (int i = rowStart; i <= rowEnd; i++) {
      if ((i != c) && (isLamp(r, i))) {
        return true;
      }
    }

    // Checking Columns - Start of the section
    current = r;
    int colStart = r;
    while ((current > 0) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(--current, c) == CellType.CORRIDOR)) {}
    if ((current == 0) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)) {
      colStart = current;
    } else {
      colStart = current + 1;
    }

    // Checking Columns - End of the section
    current = r;
    int colEnd = r;
    while ((current < height - 1) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)
        && (this.activePuzzle.getCellType(++current, c) == CellType.CORRIDOR)) {}
    if ((current == height - 1) && (this.activePuzzle.getCellType(current, c) == CellType.CORRIDOR)) {
      colEnd = current;
    } else {
      colEnd = current - 1;
    }

    // Checking for illegal bulbs in the column section
    for (int i = colStart; i <= colEnd; i++) {
      if ((i != r) && (isLamp(i, c))) {
        return true;
      }
    }

    // No illegal bulbs found
    return false;
  }

  @Override
  public Puzzle getActivePuzzle() {
    return this.activePuzzle;
  }

  @Override
  public int getActivePuzzleIndex() {
    return this.activePuzzleIndex;
  }

  @Override
  public void setActivePuzzleIndex(int index) {
    this.activePuzzleIndex = index;
    this.activePuzzle = this.library.getPuzzle(this.activePuzzleIndex);

    for (ModelObserver observer : this.observers) {
      observer.update(this);
    }
  }

  @Override
  public int getPuzzleLibrarySize() {
    return this.library.size();
  }

  @Override
  public void resetPuzzle() {
    Map<Integer, ArrayList<Integer>> bulbs = this.bulbList.get(this.activePuzzleIndex);

    bulbs.clear();

    for (ModelObserver observer : this.observers) {
      observer.update(this);
    }
  }

  @Override
  public boolean isSolved() {
    int height = this.activePuzzle.getHeight();
    int width = this.activePuzzle.getWidth();
    CellType cell;

    for (int r = 0; r < height; r++) {
      for (int c = 0; c < width; c++) {
        cell = this.activePuzzle.getCellType(r, c);

        if (cell == CellType.CORRIDOR) {
          if ((!isLit(r, c)) || (isLamp(r, c) && isLampIllegal(r, c))) {
            return false;
          }
        } else if (cell == CellType.CLUE) {
          if (!isClueSatisfied(r, c)) {
            return false;
          }
        }
      }
    }

    return true;
  }

  @Override
  public boolean isClueSatisfied(int r, int c) {
    if ((r < 0) || (c < 0) || (r >= this.activePuzzle.getHeight()) || (c >= this.activePuzzle.getWidth())) {
      throw new IndexOutOfBoundsException();
    }

    CellType cell = this.activePuzzle.getCellType(r, c);
    if (cell != CellType.CLUE) {
      throw new IllegalArgumentException();
    }

    int goal = this.activePuzzle.getClue(r, c);
    int bulbNum = 0;

    try {
      if (isLamp(r - 1, c)) {
        bulbNum++;
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {}

    try {
      if (isLamp(r + 1, c)) {
        bulbNum++;
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {}

    try {
      if (isLamp(r, c - 1)) {
        bulbNum++;
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {}

    try {
      if (isLamp(r, c + 1)) {
        bulbNum++;
      }
    } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {}

    return goal == bulbNum;
  }

  @Override
  public void addObserver(ModelObserver observer) {
    if (observer == null) {
      throw new IllegalArgumentException();
    } else {
      this.observers.add(observer);
    }
  }

  @Override
  public void removeObserver(ModelObserver observer) {
    if (observer == null || !this.observers.contains(observer)) {
      throw new IllegalArgumentException();
    } else {
      this.observers.remove(observer);
    }
  }
}
